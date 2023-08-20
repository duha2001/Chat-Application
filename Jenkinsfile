pipeline {
    agent any
    environment {
         RECIPIENTS = 'ext.tri.t.luong@dektech.com.au'
       }
    stages {
        stage('Clean') {
            steps {
                bat './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                bat './gradlew assembleDebug assembleRelease'
                archiveArtifacts '**/*.apk'
            }
        }
        stage('Test') {
            steps {
                bat './gradlew connectedAndroidTest -i'

            }
            post {
                always {
                    junit  '**/TEST-*.xml'
                }
            }
        }
    }

    post {
        success  {
            echo 'Send Email SUCCESSFUL'
            emailext (
                attachLog: true,
                to: "${RECIPIENTS}",
                attachmentsPattern: '**/index.html',
                body:  readFile("app/build/reports/androidTests/connected/index.html"),
                mimeType: 'text/html',
                subject: 'DFIVE - SUCCESSFUL'
            )
        }
        failure {
            echo 'Send Email ERROR'
            emailext (
                attachLog: true,
                to: "${RECIPIENTS}",
                attachmentsPattern: '**/index.html',
                body:  readFile("app/build/reports/androidTests/connected/index.html"),
                 mimeType: 'text/html',
                subject: 'DFIVE - ERROR'
            )
        }
    }
}