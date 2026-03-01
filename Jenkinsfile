pipeline {
    agent {
        docker { 
            image 'ghcr.io/graalvm/native-image-community:25-ol9'
            args '-v /var/lib/jenkins/.m2:/root/.m2'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Native Binary') {
            steps {
                script {
                    sh '#!/bin/bash \n./mvnw clean package -Pnative -DskipTests'
                }
            }
        }
        
        stage('Prepare Deployment') {
            steps {
                sh '#!/bin/bash \ntar -czf ansenfs-binary.tar.gz target/ansenfs'
                archiveArtifacts artifacts: 'ansenfs-binary.tar.gz', fingerprint: true
            }
        }
    }
}