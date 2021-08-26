pipeline {
    agent any
    stages {
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/user-service.git'
            }
        }
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
                    //sh 'cp -r /var/lib/jenkins/workspace/user-service-job/users-api/target .'
                    sh "aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 644684002839.dkr.ecr.us-east-2.amazonaws.com"
                    sh 'docker build -t user-service .'
                    sh 'docker tag user-service:latest 644684002839.dkr.ecr.us-east-2.amazonaws.com/user-service:latest'
                    sh 'docker push 644684002839.dkr.ecr.us-east-2.amazonaws.com/user-service:latest'
                }
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