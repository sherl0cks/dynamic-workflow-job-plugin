node {
	stage ('Code Checkout'){ checkout scm }

	stage ('Build App'){
		sh 'oc whoami -t > apiTokenOutput.txt'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		sh 'oc login 10.1.2.2:8443 --insecure-skip-tls-verify=true --username=admin --password=$OPENSHIFT_PASSWORD'

		echo 'Using build tool: mvn-3'
		def toolHome = tool 'mvn-3'
		sh "${toolHome}/bin/mvn clean deploy"
	}

	stage ('Build Image and Deploy to Dev'){
		echo 'No buildImageCommands, using default OpenShift image build and deploy'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		openshiftBuild apiURL: '10.1.2.2:8443', authToken: apiToken, bldCfg: 'cool-application-name', checkForTriggeredDeployments: 'true', namespace: 'dev-project', showBuildLogs: 'true'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: apiToken, depCfg: 'cool-application-name', namespace: 'dev-project'
	}

	stage ('Deploy to stage-project') {
		input 'Deploy to stage-project?'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		openshiftTag apiURL: '10.1.2.2:8443', authToken: apiToken, destStream: 'cool-application-name', destTag: 'latest', destinationAuthToken: apiToken, destinationNamespace: 'stage-project', namespace: 'dev-project', srcStream: 'cool-application-name', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: apiToken, depCfg: 'cool-application-name', namespace: 'stage-project'
	}

	stage ('Deploy to prod-project') {
		input 'Deploy to prod-project?'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		openshiftTag apiURL: '10.1.2.2:8443', authToken: apiToken, destStream: 'cool-application-name', destTag: 'latest', destinationAuthToken: apiToken, destinationNamespace: 'prod-project', namespace: 'stage-project', srcStream: 'cool-application-name', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: apiToken, depCfg: 'cool-application-name', namespace: 'prod-project'
	}
}