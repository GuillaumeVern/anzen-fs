import groovy.json.JsonSlurper

pipeline {
    agent any
    
    tools {
        jdk 'graalvmce-25.0.2' 
    }
    
    environment {
        LANG = 'en_US.UTF-8'
        LC_ALL = 'en_US.UTF-8'
        JAVA_HOME = "${env.JAVA_HOME}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Debug Env') {
            steps {
                script {
                    echo "Checking JAVA_HOME value:"
                    sh 'echo $JAVA_HOME'
                    sh 'ls -ld $JAVA_HOME'
                    sh '$JAVA_HOME/bin/java -version'
                }
            }
        }
        
        stage('Build Native Binary') {
            steps {
                script {
                    sh './mvnw clean package -Pnative -DskipTests native:compile'
                }
            }
        }
        
        stage('Archive Binary') {
            steps {
                archiveArtifacts artifacts: 'target/ansenfs', fingerprint: true
            }
        }

        stage('Upload to GitHub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: '73183d21-d863-4fee-b138-d395cc209e0a', usernameVariable: 'GIT_USER', passwordVariable: 'GITHUB_TOKEN')]) {
                        def repo = "GuillaumeVern/ansen-fs"
                        def binaryPath = "target/ansenfs"
                        def tagName = "v1.0.${BUILD_NUMBER}"



                        def releaseResponse = httpRequest(
                          url: "https://api.github.com/repos/${repo}/releases",
                          customHeaders: [[name: 'Authorization', value: "token ${GITHUB_TOKEN}"]],
                          httpMode: "POST",
                          requestBody: "{\"tag_name\": \"${tagName}\", \"name\": \"build ${tagName}\", \"draft\": false, \"prerelease\": false}"
                        )

                        def releaseJson = readJSON text: response.content
                        def releaseID = releaseJson.id

                        echo "created release with id: ${releaseID}"

                        httpRequest(
                          url: "https://uploads.github.com/repos/${repo}/releases/${releaseID}/assets?name=ansenfs",
                          customHeaders: [[name: 'Authorization', value: "token ${GITHUB_TOKEN}"]],
                          httpMode: "POST",
                          contentType: "APPLICATION_OCTETSTREAM",
                          uploadFile: "${binaryPath}"
                        )
                    }
                }
            }
        }
    }
}
