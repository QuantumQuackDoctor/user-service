pipeline {
    agent any
    environment {
        AWS_REGION='us-east-2'
    }
    stages {
        stage('test') {
            steps {
                sh "mvn clean test"
            }
        }
        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "mvn sonar:sonar"    
                }    
            }    
        }
        stage('Quality Gate') {
            steps {
                waitForQualityGate abortPipeline= true
            }   
        }
        stage('package') {
            steps {
                sh "mvn clean package"
            }   
        }
        stage('ECR Push') {
            steps{
                script {
                    withCredentials([string(credentialsId: '143004d8-0a84-4e71-836d-24e128adc8bb', variable: 'AWS_ID')]) {
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                        sh "docker build -t user-service ."
                        sh "docker tag user-service:latest ${AWS_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/user-service:latest"
                        sh "docker push ${AWS_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/user-service:latest"
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh 'docker context use qqd'
                sh 'docker compose up'
            }
        }
    }
    post {
        success {
            script {
                sh 'docker image prune -f -a'
            }
        }
    }
}