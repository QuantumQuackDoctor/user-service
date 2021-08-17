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
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/user-service.git'
            }
        }
        stage('build') {
            steps {
                sh "mvn clean install"
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
        stage('docker') {
            steps{
                script {
                    sh 'cp -r /var/lib/jenkins/workspace/user-service-job/users-api/target .'
                    sh 'docker build . -t quangmtran36/qqd-user-service:$Docker_tag'
                    withCredentials([usernameColonPassword(credentialsId: '46721695-0273-43dc-a462-33947e9bb8b4', variable: 'docker_credentials')]) {
                        sh 'docker login -u quangmtran36 -p $docker_credentials'
				        sh 'docker push quangmtran36/qqd-user-service:$Docker_tag'
                    }
                }
            }
        }
    
        stage('aws') {
            steps {
                echo "aws"
                //sh "mvn clean test"
            }
        }
    }
}