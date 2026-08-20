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
            description: '''Cucumber tags to filter test execution.
                Examples: '@web', '@mobile', '@sanity'.'''
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['staging', 'qa', 'dev', 'prod'],
            description: '''Target environment.'''
        )
        booleanParam(
            name: 'RUN_MOBILE',
            defaultValue: false,
            description: 'Check this to boot the Android emulator and run mobile/Appium tests.'
        )
        string(
            name: 'AVD_NAME',
            defaultValue: 'Pixel_6_API_34',
            description: 'Name of the Android Virtual Device (AVD) configured on the Jenkins agent node.'
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
        )
    }

    environment {
        JAVA_HOME         = tool 'JDK-21'
        CUCUMBER_TAGS_ENV = "${params.CUCUMBER_TAGS}"
        TARGET_ENV        = "${params.ENVIRONMENT}"
        ANDROID_HOME      = "C:/Users/mural/AppData/Local/Android/Sdk"
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

        stage('Install Playwright & System Dependencies') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'npx playwright install-deps chromium'
                        sh './gradlew installPlaywrightBrowsers --no-daemon'
                    } else {
                        bat 'gradlew.bat installPlaywrightBrowsers --no-daemon'
                    }
                }
            }
        }

        stage('Start Android Emulator') {
            when {
                expression { return params.RUN_MOBILE }
            }
            steps {
                script {
                    echo "Starting Android Emulator: ${params.AVD_NAME}..."
                    if (isUnix()) {
                        sh """
                            # Boot emulator in background
                            \$ANDROID_HOME/emulator/emulator -avd ${params.AVD_NAME} -no-window -no-audio -no-snapshot -delay-adb > /dev/null 2>&1 &

                            # Wait until adb detects device and boot is complete
                            \$ANDROID_HOME/platform-tools/adb wait-for-device

                            echo "Waiting for emulator boot completion..."
                            while [ "\$(\$ANDROID_HOME/platform-tools/adb shell getprop sys.boot_completed 2>&1 | tr -d '\r')" != "1" ]; do
                                sleep 3
                            done
                            echo "Android Emulator booted successfully!"
                        """
                    } else {
                        def winSdk = env.ANDROID_HOME.replace('/', '\\')

                        bat """
                            @echo off
                            set ANDROID_SDK_WIN=${winSdk}

                            rem Launch emulator in detached background process
                            start "" "%ANDROID_SDK_WIN%\\emulator\\emulator.exe" -avd ${params.AVD_NAME} -no-window -no-audio -no-snapshot

                            echo Waiting for ADB device recognition...
                            "%ANDROID_SDK_WIN%\\platform-tools\\adb.exe" wait-for-device

                            echo Waiting for Android OS boot completion...
                            :LOOP
                            for /f "tokens=*" %%a in ('"%ANDROID_SDK_WIN%\\platform-tools\\adb.exe" shell getprop sys.boot_completed 2^>nul') do set BOOT_STATE=%%a
                            if not "%BOOT_STATE%"=="1" (
                                timeout /t 3 /nobreak >nul
                                goto LOOP
                            )
                            echo Android Emulator booted successfully!
                        """
                    }
                }
            }
        }

        stage('Execute Tests') {
            environment {
                DB_PASSWORD = credentials('ENTERPRISE_DB_PASSWORD')
            }
            steps {
                script {
                    def cleanTask = params.CLEAN_BUILD ? 'clean' : ''

                    if (isUnix()) {
                        sh """
                            xvfb-run ./gradlew ${cleanTask} test \
                              "-Dcucumber.filter.tags=${env.CUCUMBER_TAGS_ENV}" \
                              "-Denv=${env.TARGET_ENV}" \
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
            script {
                // Kill emulator if it was started during build
                if (params.RUN_MOBILE) {
                    echo "Cleaning up Android Emulator instance..."
                    if (isUnix()) {
                        sh '$ANDROID_HOME/platform-tools/adb emu kill || true'
                    } else {
                        def winSdk = env.ANDROID_HOME.replace('/', '\\')
                        bat "@echo off\n\"${winSdk}\\platform-tools\\adb.exe\" emu kill || exit /b 0"
                    }
                }
            }

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
                def attachmentPattern = extentExists ? extentReportPath : ''

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

${extentExists ? 'Please find the interactive Extent Report attached to this email for full stack traces.' : '⚠️ Note: Interactive Extent Report attachment is missing because the build or execution cycle was cut short.'}

Regards,
QA Automation
""",
                        attachmentsPattern: attachmentPattern
                    )
                }

                cleanWs(deleteDirs: true, notFailBuild: true)
            }
        }
    }
}