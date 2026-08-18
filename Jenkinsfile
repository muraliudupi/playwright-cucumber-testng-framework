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
                        sh '''
                            npx playwright install-deps chromium
                            npx playwright install chromium
                        '''
                    } else {
                        bat '''
                            call npx playwright install chromium
                        '''
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
                def extentExists = fileExists('build/reports/extent/ExtentReport.html')
                def attachmentPath = extentExists ? 'build/reports/extent/ExtentReport.html' : ''

                emailext (
                    to: "${params.NOTIFICATION_EMAIL}",
                    subject: "Automation Results: ${currentBuild.currentResult} | ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                        Hi Team,

                        The automation test execution has completed.

                        =========================================
                        📊 EXECUTION SUMMARY
                        =========================================
                        • Status:         ${currentBuild.currentResult}
                        • Job Name:       ${env.JOB_NAME}
                        • Build Tag:      ${env.BUILD_TAG}
                        • Execution Tags: ${params.CUCUMBER_TAGS}
                        =========================================

                        View Jenkins Job details: ${env.BUILD_URL}

                        Regards,
                        QA Automation Bot
                    """,
                    attachmentsPattern: attachmentPath,
                    mimeType: 'text/plain'
                )
            }
        }
    }
}