   def getDockerTag() {
        def tag = sh script: 'git rev-parse HEAD', returnStdout: true
        return tag
    }

pipeline {
    agent any
    environment{
	    Docker_tag = getDockerTag()
    }
    stages {
    
        stage('ECR Login') {
            steps {
                script {
                    sh "aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 644684002839.dkr.ecr.us-east-2.amazonaws.com"
                }
            }
        }
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/user-service.git'
            }
        }
        stage('package') {
            steps {
                sh "mvn clean package"
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
        stage('ECR Push') {
            steps{
                script {
                    sh 'cp -r /var/lib/jenkins/workspace/user-service-job/users-api/target .'
                    sh 'docker build . -t quangmtran36/qqd-user-service:$Docker_tag'
                    sh 'docker tag user-service:latest 644684002839.dkr.ecr.us-east-2.amazonaws.com/user-service:latest'
                    sh 'docker push 644684002839.dkr.ecr.us-east-2.amazonaws.com/user-service:latest'
                }
            }
            post {
                success {
                    sh 'docker rmi $(docker images -a | grep aws | awk '{print $3}')'
                }
            }
        }
    }
}