pipeline {
    agent any  

    tools {
        maven 'maven-3.9.9'
    }

    environment {
        CHROME_BIN = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'  // Adjust if needed
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/tusharS007/Selenium-API-Test-Framework.git'
            }
        }

        stage('Verify Chrome Installation') {
            steps {
                bat 'where chrome'
                bat 'chrome --version'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('Test') {
            options {
                timeout(time: 10, unit: 'MINUTES') // abort stuck test stage
            }
            steps {
                bat 'mvn test'
            }
        }

        stage('Reports') {
            steps {
                publishHTML(target: [
                    reportDir: 'src/test/resources/ExtentReport',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'Extent Spark Report'
                ])
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/src/test/resources/ExtentReport/*.html', fingerprint: true
            junit 'target/surefire-reports/*.xml'
        }

        success {
            emailext (
                to: 'testemail@gmail.com',
                subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                <html>
                <body>
                <p>Hello Team,</p>

                <p>The latest Jenkins build has completed successfully.</p>
                <p><b>Project Name:</b> ${env.JOB_NAME}</p>
                <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                <p><b>Status:</b> <span style="color: green;"><b>SUCCESS</b></span></p>
                <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                <p><b>Extent Report:</b> <a href="${env.BUILD_URL}artifact/src/test/resources/ExtentReport/ExtentReport.html">Click here</a></p>

                <p>Best regards,</p>
                <p><b>Automation Team</b></p>
                </body>
                </html>
                """,
                mimeType: 'text/html',
                attachLog: true
            )
        }

        failure {
            emailext (
                to: 'testemail@gmail.com',
                subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                <html>
                <body>
                <p>Hello Team,</p>

                <p>The Jenkins build has <b style="color: red;">FAILED</b>.</p>
                <p><b>Project:</b> ${env.JOB_NAME}</p>
                <p><b>Build:</b> #${env.BUILD_NUMBER}</p>
                <p><b>Status:</b> <span style="color: red;"><b>FAILED &#10060;</b></span></p>
                <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                <p><b>Extent Report (if available):</b> <a href="${env.BUILD_URL}artifact/src/test/resources/ExtentReport/ExtentReport.html">Click here</a></p>

                <p>Please check the logs for more details.</p>
                <p>Regards,<br><b>Automation Team</b></p>
                </body>
                </html>
                """,
                mimeType: 'text/html',
                attachLog: true
            )
        }
    }
}