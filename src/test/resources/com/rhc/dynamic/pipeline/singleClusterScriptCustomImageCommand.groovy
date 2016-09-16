OpenShiftClient oc = new com.rhc.automation.clients.OpenShiftClient()
DockerClient docker = new com.rhc.automation.clients.DockerClient()

node {
	stage 'Code Checkout'
	checkout scm
	
	stage 'Build App'
	oc.login( 'master.openshift.redhat.com' )
	docker.login('registry.apps.redhat.com', oc.getTrimmedUserToken() )
	
	dir( 'build-home-dir' ) {
		echo 'No build tool declared. Any commands will execute directly in the shell.'
		sh "customCommand"
		sh "customCommand with arguments"
	}
	
	stage 'Build Image and Deploy to Dev'
	echo 'Found buildImageCommands, executing in shell'
	sh 'customCommand'
	sh 'customCommand with arguments'
}
