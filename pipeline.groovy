pipeline 
{
    agent any
    tools 
    {
  maven 'jenkins-maven'
  jdk 'java'
    }
   
  stages {
      stage('clone git hub rep')
      {
        steps 
        {
            git 'https://github.com/parthac1/dockeransiblejenkins.git'
        }

      } 

      stage('build maven')
      {
        steps 
        {
             sh 'mvn clean install'
        }

      }


      stage('test maven')
      {
        steps 
        {
             sh 'mvn test'
        }

      }
     
      stage('code quality analysis')
      {
       steps {
                withSonarQubeEnv('sonar') 
                    {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        
      stage("Quality Gate") {
            steps {
              timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate abortPipeline: true
              }
            }
          }
  
       stage('docker build')
      {
        steps 
        {
            sh 'docker build . -t parthac1/artifacts:v1'
        }

      }
        
       stage('docker push')
      {
        steps 
        {
             withCredentials([string(credentialsId: 'docker-hub', variable: 'dockerhub')]) {
              sh " docker login -u parthac1 -p ${dockerhub}"
            }
             
             
             
             sh 'docker push parthac1/artifacts:tagname'
        }

      }
  
  
  
  
  }    
}