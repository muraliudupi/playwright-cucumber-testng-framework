pipeline {
    agent { label 'android-emulator' }

    options {
        timeout(time: 90, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    parameters {
        string(
            name: 'CUCUMBER_TAGS',
            defaultValue: '@sanity and not @wip',
            description: 'Cucumber tags to filter test execution.'
                'NOTE: @sanity currently only exists on 4 web scenarios (web_login x2, web_transfer, web_logout) — the default run does not exercise mobile at all.'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['staging', 'qa', 'dev', 'prod'],
            description: 'Target environment.'
                'NOTE: Not currently consumed anywhere in ConfigReader/config.properties. Intended wiring for future usage.'
        )
        booleanParam(
            name: 'CLEAN_BUILD',
            defaultValue: true,
            description: 'Run gradlew clean before executing tests'
        )
        string(
            name: 'NOTIFICATION_EMAIL',
            defaultValue: '',
            description: 'Recipient address for the test report email.'
                'Required — the build fails fast in the first stage if left blank, rather than silently emailing nobody or erroring inside the email step.'
        )
    }

    environment {
        JAVA_HOME           = tool 'JDK-21'
        CUCUMBER_TAGS_ENV   = "${params.CUCUMBER_TAGS}"
        TARGET_ENV          = "${params.ENVIRONMENT}"
    }

    stages {

        stage('Validate Parameters') {
            steps {
                script {
                    if (!params.NOTIFICATION_EMAIL?.trim()) {
                        error("NOTIFICATION_EMAIL is required — set it when triggering the build.")
                    }
                }
            }
        }

        stage('Checkout Baseline') {
            steps {
                checkout scm
            }
        }

        stage('Install System & Playwright Dependencies') {
            steps {
                script {
                    if (isUnix()) {
                        // Scoped to chromium for now.
                        sh 'npx playwright install-deps chromium'
                        sh './gradlew installPlaywrightBrowsers --no-daemon'
                    } else {
                        echo 'Skipping CLI OS-dependency install on Windows agents (not applicable).'
                        bat 'gradlew.bat installPlaywrightBrowsers --no-daemon'
                    }
                }
            }
        }

        stage('Execute Playwright Tests') {
            environment {
                DB_PASSWORD = credentials('ENTERPRISE_DB_PASSWORD')
            }
            steps {
                script {
                    def cleanTask = params.CLEAN_BUILD ? 'clean' : ''

                    if (isUnix()) {
                        sh """
                            xvfb-run ./gradlew ${cleanTask} test \
                              "-Dcucumber.filter.tags=\$CUCUMBER_TAGS_ENV" \
                              "-Denv=\$TARGET_ENV" \
                              --no-daemon
                        """
                    } else {
                        bat """
                            gradlew.bat ${cleanTask} test "-Dcucumber.filter.tags=%CUCUMBER_TAGS_ENV%" "-Denv=%TARGET_ENV%" --no-daemon
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/reports/**/*', allowEmptyArchive: true

            junit testResults: 'build/test-results/**/*.xml', allowEmptyResults: true

            script {
                def totalCount = 0
                def passedCount = 0
                def failedCount = 0
                def skippedCount = 0
                def emailStatus = "⚠️ ERROR (No Report Found)"

                def jsonFiles = findFiles(glob: 'build/reports/cucumber/*.json')

                if (jsonFiles.length > 0) {
                    jsonFiles.each { file ->
                        try {
                            def fileContent = readFile(file.path)
                            def slurper = new groovy.json.JsonSlurper()
                            def parsedJson = slurper.parseText(fileContent)

                            parsedJson.each { feature ->
                                feature.elements?.each { element ->
                                    if (element.type == 'scenario') {
                                        totalCount++
                                        def steps = element.steps ?: []
                                        boolean hasFailed = steps.any { it.result?.status == 'failed' }
                                        boolean hasSkippedLike = steps.any {
                                            ['skipped', 'pending', 'undefined', 'ambiguous'].contains(it.result?.status)
                                        }
                                        if (hasFailed) {
                                            failedCount++
                                        } else if (hasSkippedLike) {
                                            skippedCount++
                                        } else {
                                            passedCount++
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            echo "Error parsing ${file.path}: ${e.message}"
                        }
                    }

                    if (totalCount == 0) {
                        emailStatus = "⚠️ WARNING (0 Scenarios Matched — check CUCUMBER_TAGS or report parsing)"
                        currentBuild.result = 'UNSTABLE'
                    } else if (failedCount > 0) {
                        emailStatus = "❌ FAILED (${failedCount} Scenarios Failed)"
                    } else {
                        emailStatus = "✅ PASSED (All Scenarios Clean)"
                    }
                }

                env.TOTAL_COUNT   = totalCount.toString()
                env.PASSED_COUNT  = passedCount.toString()
                env.FAILED_COUNT  = failedCount.toString()
                env.SKIPPED_COUNT = skippedCount.toString()
                env.EMAIL_STATUS  = emailStatus

                def extentReportPath = 'build/reports/extent/ExtentReport.html'
                def extentExists = fileExists(extentReportPath)
                env.ATTACHMENT_PATH = extentExists ? extentReportPath : ''
                if (!extentExists) {
                    echo '⚠️ WARNING: ExtentReport.html was not generated — likely a compilation or early framework failure.'
                }
            }

            withCredentials([
                    string(credentialsId: 'MAIL_USERNAME', variable: 'MAIL_USER'),
                    string(credentialsId: 'MAIL_PASSWORD', variable: 'MAIL_PASS')
                ]) {
                emailext(
                    subject: "Automation Results: ${env.EMAIL_STATUS} | ${env.JOB_NAME} (Build #${env.BUILD_NUMBER})",
                    to: params.NOTIFICATION_EMAIL,
                    from: "Automation Framework Jenkins <${MAIL_USER}>",
                    mimeType: 'text/plain',
                    body: """Hi Team,

The automation test execution has completed.

=========================================
📊 EXECUTION SUMMARY
=========================================
• Status:            ${env.EMAIL_STATUS}
• Total Scenarios:   ${env.TOTAL_COUNT}
• Passed Scenarios:  ${env.PASSED_COUNT}
• Failed Scenarios:  ${env.FAILED_COUNT}
• Skipped Scenarios: ${env.SKIPPED_COUNT}
=========================================

- Job:          ${env.JOB_NAME}
- Build:        #${env.BUILD_NUMBER}
- Build Link:   ${env.BUILD_URL}

${env.ATTACHMENT_PATH ? 'Please find the interactive Extent Report attached to this email for full stack traces.' : '⚠️ Note: Interactive Extent Report attachment is missing because the build or execution cycle was cut short.'}

Regards,
QA Automation
""",
                    attachmentsPattern: env.ATTACHMENT_PATH
                )
            }

            cleanWs(deleteDirs: true, notFailBuild: true)
        }
    }
}