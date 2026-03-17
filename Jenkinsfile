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

                        sh """
                        curl -X POST \
                          -H "Authorization: token ${GITHUB_TOKEN}" \
                          -H "Content-Type: application/json" \
                          -d '{"tag_name": "${tagName}", "name": "Build ${tagName}", "draft": false, "prerelease": false}' \
                          https://api.github.com/repos/${repo}/releases
                        """
                        
                        def releaseID = sh """
                        echo \$(curl -L \
                          -H "Accept: application/vnd.github+json" \
                          -H "Authorization: Bearer ${GITHUB_TOKEN}" \
                          -H "X-GitHub-Api-Version: 2026-03-10" \
                          https://api.github.com/repos/${repo}/releases/tags/${tagName})
                        """


                        sh """
                        curl -X POST \
                          -H "Accept: application/vnd.github+json" \
                          -H "Authorization: token ${GITHUB_TOKEN}" \
                          -H "Content-Type: application/octet-stream" \
                          -H "X-GitHub-Api-Version: 2026-03-10" \
                          https://uploads.github.com/repos/${repo}/releases/tags/${tagName}/assets?name=ansenfs \
                          --data-binary @${binaryPath}
                        """
                    }
                }
            }
        }
    }
}
