node {
	stage ('Code Checkout') { checkout scm }

	stage ('Build App') {
		sh 'oc whoami -t > apiTokenOutput.txt'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		sh 'oc login 10.1.2.2:8443 --insecure-skip-tls-verify=true --username=admin --password=$OPENSHIFT_PASSWORD'

		dir( 'build-home-dir' ) {
			echo 'Using build tool: sh'
			sh "customBuildAppCommand"
			sh "customBuildAppCommand with arguments"
		}
	}

	stage ('Build Image and Deploy to Dev') {
		echo 'Found buildImageCommands, executing in shell'
		sh 'customBuildImageCommand'
		sh 'customBuildImageCommand with arguments'
	}

	stage ('Deploy to stage-project') {
		input 'Deploy to stage-project?'
		sh 'customDeployImageCommand'
		sh 'customDeployImageCommand with arguments'
	}

	stage ('Deploy to prod-project') {
		input 'Deploy to prod-project?'
		sh 'customDeployImageCommand'
		sh 'customDeployImageCommand with arguments'
	}
}
