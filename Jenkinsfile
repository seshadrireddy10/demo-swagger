pipeline {
    agent any

    environment {
        // 1. REPLACE with your actual Docker Hub username
        DOCKER_HUB_USER = 'reddyp'
        IMAGE_NAME      = 'demo-swagger'
        
        // 2. This must match the Credentials ID you set up in Jenkins
        DOCKER_HUB_CREDS = 'DOCKER_HUB_CREDS'
    }

    stages {
        stage('Set Build Name as Date') {
            steps {
                script {
                    def dateStr = new Date().format("yyyy-MM-dd")
                    currentBuild.displayName = "${dateStr}-${env.BUILD_NUMBER}"
                }
            }
        }
        stage('Clone the code') {
            steps {
                git 'https://github.com/seshadrireddy10/demo-swagger.git'
            }
        }
        stage('build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                echo "welcome to Test cases...."
            }
        }
        stage('build the docker images') {
            steps {
                // Docker Hub requires the username prefix to accept pushes
                sh "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${currentBuild.displayName} ."
            }
        }
        stage('validate docker run') {
            steps {
                script {
                    sh "docker rm -f demo-swagger-container || true"
                    // Running using the newly structured image name
                    sh "docker run -d --name demo-swagger-container -p 8081:8080 ${DOCKER_HUB_USER}/${IMAGE_NAME}:${currentBuild.displayName}"
                    sh "sleep 15"
                    
                    try {
                        sh "curl -f http://localhost:8081/actuator/health || curl -f http://localhost:8081/v2/api-docs || curl -f http://localhost:8081/"
                        echo "Validation Successful: Container is up and responding!"
                    } catch (Exception e) {
                        echo "Validation Failed: App did not respond. Fetching container logs..."
                        sh "docker logs demo-swagger-container"
                        error "Pipeline failed due to container verification failure."
                    }
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                // Securely logs into Docker Hub using Jenkins credentials store
                withCredentials([usernamePassword(credentialsId: "${DOCKER_HUB_CREDS}", passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin"
                    
                    // Pushes both tags
                    sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${currentBuild.displayName}"
                   // sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                }
            }
        }
    }

    post {
        always {
            echo "Starting pipeline cleanup..."
            sh "docker rm -f demo-swagger-container || true"
            
            // Cleans up the host machine using the new variable names
            sh "docker rmi ${DOCKER_HUB_USER}/${IMAGE_NAME}:${currentBuild.displayName} || true"
            sh "docker rmi ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest || true"
            sh "docker image prune -f"
            
            // Explicitly logs out of Docker Hub for security
            sh "docker logout || true"
        }
    }
}
