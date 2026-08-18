pipeline {
    agent any

    parameters {
        string(
            name: 'CUCUMBER_TAGS',
            defaultValue: '@sanity and not @wip',
            description: 'Cucumber tags to filter test execution'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['staging', 'qa', 'dev', 'prod'],
            description: 'Target environment'
        )
        booleanParam(
            name: 'CLEAN_BUILD',
            defaultValue: true,
            description: 'Run gradlew clean before executing tests'
        )
        string(
            name: 'NOTIFICATION_EMAIL',
            defaultValue: 'qamurali@outlook.com',
            description: 'Recipient address for test report emails'
        )
    }

    environment {
        DB_PASSWORD   = credentials('ENTERPRISE_DB_PASSWORD')
        MAIL_USER     = credentials('MAIL_USERNAME')
        MAIL_PASS     = credentials('MAIL_PASSWORD')
        JAVA_HOME     = tool 'JDK-21'
    }

    stages {
        stage('Checkout Baseline') {
            steps {
                checkout scm
            }
        }

        stage('Install System & Playwright Dependencies') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'npx playwright install-deps'
                    } else {
                        echo 'Skipping CLI browser install; Playwright Java manages versioned binaries automatically.'
                    }
                }
            }
        }

        stage('Execute Playwright Tests') {
            steps {
                script {
                    def cleanTask = params.CLEAN_BUILD ? 'clean' : ''

                    if (isUnix()) {
                        sh """
                            xvfb-run ./gradlew ${cleanTask} test \
                              "-Dcucumber.filter.tags=${params.CUCUMBER_TAGS}" \
                              "-Denv=${params.ENVIRONMENT}" \
                              --no-daemon
                        """
                    } else {
                        bat """
                            gradlew.bat ${cleanTask} test "-Dcucumber.filter.tags=${params.CUCUMBER_TAGS}" "-Denv=${params.ENVIRONMENT}" --no-daemon
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/reports/**/*', allowEmptyArchive: true

            script {
                // 1. Parse Cucumber JSON results to capture counts and status text
                def totalCount = 0
                def passedCount = 0
                def failedCount = 0
                def emailStatus = "⚠️ ERROR (No Report Found)"

                def jsonFiles = findFiles(glob: 'build/reports/cucumber/*.json')

                if (jsonFiles.length > 0) {
                    def jsonSlurper = new groovy.json.JsonSlurper()
                    jsonFiles.each { file ->
                        def parsedJson = jsonSlurper.parse(file.path)
                        parsedJson.each { feature ->
                            feature.elements?.each { element ->
                                if (element.type == 'scenario') {
                                    totalCount++
                                    boolean hasFailedStep = element.steps?.any { it.result?.status == 'failed' }
                                    if (hasFailedStep) {
                                        failedCount++
                                    } else {
                                        passedCount++
                                    }
                                }
                            }
                        }
                    }

                    if (failedCount > 0) {
                        emailStatus = "❌ FAILED (${failedCount} Scenarios Failed)"
                    } else {
                        emailStatus = "✅ PASSED (All Scenarios Clean)"
                    }
                }

                // 2. Check for Extent Report attachment & message setup
                def extentExists = fileExists('build/reports/extent/ExtentReport.html')
                def attachmentPath = extentExists ? 'build/reports/extent/ExtentReport.html' : ''

                def extentNote = extentExists ?
                    'Please find the interactive Extent Report attached to this email for full stack traces.' :
                    '⚠️ Note: Interactive Extent Report attachment is missing because the build or execution cycle was cut short.'

                // 3. Construct GitHub-aligned Body
                def emailBody = """Hi Team,

                    The automation test execution has completed.

                    =========================================
                            📊 EXECUTION SUMMARY
                    =========================================
                    • Status:            ${emailStatus}
                    • Total Scenarios:   ${totalCount}
                    • Passed Scenarios:  ${passedCount}
                    • Failed Scenarios:  ${failedCount}
                    =========================================

                    - Repository:   ${env.JOB_NAME}
                    - Branch:       ${env.GIT_BRANCH ?: 'N/A'}
                    - Triggered By: ${env.BUILD_USER_ID ?: 'Jenkins'}
                    - Action Link:  ${env.BUILD_URL}

                    ${extentNote}

                    Regards,
                    QA Automation"""

                // 4. Send Email matching GitHub formatting style
                emailext (
                    to: "${params.NOTIFICATION_EMAIL}",
                    subject: "Automation Results: ${emailStatus} | ${env.JOB_NAME} (Build #${env.BUILD_NUMBER})",
                    body: emailBody,
                    attachmentsPattern: attachmentPath,
                    mimeType: 'text/plain'
                )
            }
        }
    }
}