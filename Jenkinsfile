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
            defaultValue: '@mobile and @login',
            description: '''Cucumber tags to filter test execution. Examples: '@web', '@mobile', '@sanity'.'''
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['staging', 'qa', 'dev', 'prod'],
            description: 'Target environment.'
        )
        booleanParam(
            name: 'CLEAN_BUILD',
            defaultValue: true,
            description: 'Run gradlew clean before executing tests'
        )
        string(
            name: 'NOTIFICATION_EMAIL',
            defaultValue: 'qamurali@outlook.com',
            description: 'Recipient address for the test report email'
        )
        booleanParam(
            name: 'RUN_MOBILE',
            defaultValue: false,
            description: 'Check this to boot the Android emulator and run mobile/Appium tests.'
        )
        choice(
            name: 'PARALLEL_DEVICES',
            choices: ['2', '1'],
            description: 'Number of Android Emulators to boot for parallel execution.'
        )
    }

    environment {
        JAVA_HOME         = tool 'JDK-21'
        CUCUMBER_TAGS_ENV = "${params.CUCUMBER_TAGS}"
        TARGET_ENV        = "${params.ENVIRONMENT}"
        ANDROID_HOME      = "C:/Users/mural/AppData/Local/Android/Sdk"
        APP_PATH          = "D:/Automation/playwright-cucumber-testng-framework/src/test/resources/apps/mda-2.2.0-25.apk"
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

        stage('Stage Application APK') {
            when {
                expression { return params.RUN_MOBILE }
            }
            steps {
                script {
                    if (isUnix()) {
                        sh """
                            mkdir -p src/test/resources/apps
                            cp "${env.APP_PATH}" src/test/resources/apps/mda-2.2.0-25.apk
                        """
                    } else {
                        def winSource = env.APP_PATH.replace('/', '\\')
                        bat """
                            @echo off
                            if not exist "src\\test\\resources\\apps" mkdir "src\\test\\resources\\apps"
                            copy /Y "${winSource}" "src\\test\\resources\\apps\\mda-2.2.0-25.apk"
                        """
                    }
                }
            }
        }

        stage('Start Android Emulator & Appium') {
            when {
                expression { return params.RUN_MOBILE }
            }
            steps {
                script {
                    def winSdk = env.ANDROID_HOME.replace('/', '\\')
                    def devCount = params.PARALLEL_DEVICES as Integer

                    if (isUnix()) {
                        sh """
                            appium --port 4723 > appium.log 2>&1 &
                            \$ANDROID_HOME/emulator/emulator -avd Pixel_6a -port 5554 -no-window -no-audio -no-snapshot > /dev/null 2>&1 &
                            if [ "${devCount}" -ge 2 ]; then
                                \$ANDROID_HOME/emulator/emulator -avd Pixel_6a_2 -port 5556 -no-window -no-audio -no-snapshot > /dev/null 2>&1 &
                            fi

                            echo "Waiting for Primary Emulator (emulator-5554)..."
                            \$ANDROID_HOME/platform-tools/adb -s emulator-5554 wait-for-device
                            while [ "\$(\$ANDROID_HOME/platform-tools/adb -s emulator-5554 shell getprop sys.boot_completed 2>&1 | tr -d '\r')" != "1" ]; do
                                sleep 3
                            done
                            echo "Primary Emulator booted!"

                            if [ "${devCount}" -ge 2 ]; then
                                echo "Waiting for Secondary Emulator (emulator-5556)..."
                                \$ANDROID_HOME/platform-tools/adb -s emulator-5556 wait-for-device
                                while [ "\$(\$ANDROID_HOME/platform-tools/adb -s emulator-5556 shell getprop sys.boot_completed 2>&1 | tr -d '\r')" != "1" ]; do
                                    sleep 3
                                me
                                echo "Secondary Emulator booted!"
                            fi
                            echo "All requested emulators booted successfully!"
                        """
                    } else {
                        bat """
                            @echo off
                            set ANDROID_SDK_WIN=${winSdk}

                            rem Always start Appium
                            start "" appium --port 4723

                            rem Launch Primary Emulator
                            start "" "%ANDROID_SDK_WIN%\\emulator\\emulator.exe" -avd Pixel_6a -port 5554 -no-window -no-audio -no-snapshot
                        """

                        if (devCount >= 2) {
                            bat """
                                @echo off
                                set ANDROID_SDK_WIN=${winSdk}
                                rem Launch Secondary Emulator
                                start "" "%ANDROID_SDK_WIN%\\emulator\\emulator.exe" -avd Pixel_6a_2 -port 5556 -no-window -no-audio -no-snapshot
                            """
                        }

                        bat """
                            @echo off
                            set ANDROID_SDK_WIN=${winSdk}

                            echo Waiting for Primary Emulator (emulator-5554) boot completion...
                            "%ANDROID_SDK_WIN%\\platform-tools\\adb.exe" -s emulator-5554 wait-for-device
                            :LOOP1
                            for /f "tokens=*" %%a in ('"%ANDROID_SDK_WIN%\\platform-tools\\adb.exe" -s emulator-5554 shell getprop sys.boot_completed 2^>nul') do set BOOT_STATE1=%%a
                            if not "%BOOT_STATE1%"=="1" (
                                ping 127.0.0.1 -n 4 >nul
                                goto LOOP1
                            )
                            echo Primary Emulator booted!
                        """

                        if (devCount >= 2) {
                            bat """
                                @echo off
                                set ANDROID_SDK_WIN=${winSdk}

                                echo Waiting for Secondary Emulator (emulator-5556) boot completion...
                                "%ANDROID_SDK_WIN%\\platform-tools\\adb.exe" -s emulator-5556 wait-for-device
                                :LOOP2
                                for /f "tokens=*" %%a in ('"%ANDROID_SDK_WIN%\\platform-tools\\adb.exe" -s emulator-5556 shell getprop sys.boot_completed 2^>nul') do set BOOT_STATE2=%%a
                                if not "%BOOT_STATE2%"=="1" (
                                    ping 127.0.0.1 -n 4 >nul
                                    goto LOOP2
                                )
                                echo Secondary Emulator booted!
                            """
                        }
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
                // Kill all targeted emulator instances if started during build
                if (params.RUN_MOBILE) {
                    echo "Cleaning up Android Emulator instances..."
                    def devCount = params.PARALLEL_DEVICES as Integer

                    if (isUnix()) {
                        sh """
                            ${env.ANDROID_HOME}/platform-tools/adb -s emulator-5554 emu kill || true
                        """
                        if (devCount >= 2) {
                            sh """
                                ${env.ANDROID_HOME}/platform-tools/adb -s emulator-5556 emu kill || true
                            """
                        }
                    } else {
                        def winSdk = env.ANDROID_HOME.replace('/', '\\')
                        bat """
                            @echo off
                            "${winSdk}\\platform-tools\\adb.exe" -s emulator-5554 emu kill 2>nul || exit /b 0
                        """
                        if (devCount >= 2) {
                            bat """
                                @echo off
                                "${winSdk}\\platform-tools\\adb.exe" -s emulator-5556 emu kill 2>nul || exit /b 0
                            """
                        }
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
                        from: 'Automation Framework Jenkins <${MAIL_USER}>',
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