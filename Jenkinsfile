pipeline {
    agent any

    // Defining runtime parameters directly in Jenkins
    parameters {
        string(
            name: 'CUCUMBER_TAGS',
            defaultValue: '@sanity and not @wip',
            description: 'Cucumber tags to filter test execution (e.g., @sanity, @regression, @smoke)'
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
            defaultValue: 'qa-team@example.com',
            description: 'Recipient address for test report emails'
        )
    }

    environment {
        // Fetch secrets securely from Jenkins Credential Store
        DB_PASSWORD   = credentials('ENTERPRISE_DB_PASSWORD')
        MAIL_USER     = credentials('MAIL_USERNAME')
        MAIL_PASS     = credentials('MAIL_PASSWORD')
        JAVA_HOME     = tool 'JDK-21' // Configured under Jenkins Global Tool Configuration
    }

    stages {
        stage('Checkout Baseline') {
            steps {
                checkout scm
                sh 'chmod +x gradlew'
            }
        }

        stage('Install System & Playwright Dependencies') {
            steps {
                // Ensure OS packages and Playwright Chromium binaries are available
                sh '''
                    npx playwright install-deps chromium
                    npx playwright install chromium
                '''
            }
        }

        stage('Execute Playwright Tests') {
            steps {
                script {
                    def cleanTask = params.CLEAN_BUILD ? 'clean' : ''

                    // Run Gradle test suite under virtual display (xvfb) using injected parameters
                    sh """
                        xvfb-run ./gradlew ${cleanTask} test \
                          "-Dcucumber.filter.tags=${params.CUCUMBER_TAGS}" \
                          "-Denv=${params.ENVIRONMENT}" \
                          --no-daemon
                    """
                }
            }
        }

        stage('Parse Cucumber JSON Results') {
            steps {
                script {
                    // Aggregate scenarios and status from generated Cucumber JSON files
                    def statusScript = '''
                        JSON_FILES=$(find build/reports/cucumber -name "*.json" 2>/dev/null)
                        if [ -n "$JSON_FILES" ]; then
                          RESULT=$(echo "$JSON_FILES" | xargs jq -s '
                            [.[][] | .elements[]? | select(.type == "scenario")] as $scenarios
                            | ($scenarios | map(
                                (.steps // []) as $steps
                                | if ($steps | any(.result.status == "failed")) then "failed"
                                  elif ($steps | any(.result.status == "skipped" or .result.status == "pending" or .result.status == "undefined" or .result.status == "ambiguous")) then "skipped"
                                  else "passed"
                                  end
                              )) as $statuses
                            | { total: ($statuses | length),
                                passed: ($statuses | map(select(. == "passed")) | length),
                                failed: ($statuses | map(select(. == "failed")) | length) }
                          ')
                          echo "$RESULT" > cucumber_summary.json
                        fi
                    '''
                    sh(script: statusScript, returnStatus: true)
                }
            }
        }
    }

    post {
        always {
            // Archive HTML, Cucumber, and Extent artifacts
            archiveArtifacts artifacts: 'build/reports/**/*', allowEmptyArchive: true

            // Send customizable email notifications using the Email Extension plugin
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
                        • Status:      ${currentBuild.currentResult}
                        • Job Name:    ${env.JOB_NAME}
                        • Build Tag:   ${env.BUILD_TAG}
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