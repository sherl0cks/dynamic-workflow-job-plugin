node {
	stage ('Code Checkout') {
	checkout scm
	}
	
	stage ('Build App') {
	sh 'oc whoami -t > apiTokenOutput.txt'
	String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
	sh 'oc login master.openshift.redhat.com --insecure-skip-tls-verify=true --username=admin --password=$OPENSHIFT_PASSWORD'
	sh "docker login -u=admin -e=rhc-open-innovation-labs@redhat.com -p=${apiToken} registry.apps.redhat.com"
	
	dir( 'build-home-dir' ) {
		echo 'No build tool declared. Any commands will execute directly in the shell.'
		sh "customBuildAppCommand"
		sh "customBuildAppCommand with arguments"
	}
	}
	
	stage ('Build Image and Deploy to Dev') {
	echo 'No buildImageCommands, using default OpenShift image build and deploy'
	String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
  	openshiftBuild apiURL: 'master.openshift.redhat.com', authToken: apiToken, bldCfg: 'cool-application-name', checkForTriggeredDeployments: 'true', namespace: 'dev-project', showBuildLogs: 'true'
	openshiftVerifyDeployment apiURL: 'master.openshift.redhat.com', authToken: apiToken, depCfg: 'cool-application-name', namespace: 'dev-project', replicaCount: '1', verifyReplicaCount: 'true'  
	}
	
	stage 'Deploy to stage-project'
	input 'Deploy to stage-project?'
	def currentImageRepositoryWithVersion0 = 'registry.apps.redhat.com/dev-project/cool-application-name:latest'
	def newImageRepositoryWithVersion0 = 'registry.apps.redhat.com/stage-project/cool-application-name:latest'
	docker.promoteImageBetweenRepositories( currentImageRepositoryWithVersion, newImageRepositoryWithVersion )
	
	stage 'Deploy to prod-project'
	input 'Deploy to prod-project?'
	def currentImageRepositoryWithVersion1 = 'registry.apps.redhat.com/stage-project/cool-application-name:latest'
	def newImageRepositoryWithVersion1 = 'registry.apps.redhat.com/prod-project/cool-application-name:latest'
	docker.promoteImageBetweenRepositories( currentImageRepositoryWithVersion, newImageRepositoryWithVersion )
}
