node {
	stage ('Code Checkout') {
	checkout scm
	}
	
	stage 'Build App'
	oc.login( 'master.openshift.redhat.com', 'admin', env.OPENSHIFT_PASSWORD )
	docker.login('registry.apps.redhat.com', oc.getTrimmedUserToken() )
	
	dir( 'build-home-dir' ) {
		echo 'No build tool declared. Any commands will execute directly in the shell.'
		sh "customBuildAppCommand"
		sh "customBuildAppCommand with arguments"
	}
	
	stage 'Build Image and Deploy to Dev'
	echo 'Found buildImageCommands, executing in shell'
	sh 'customBuildImageCommand'
	sh 'customBuildImageCommand with arguments'
	
	stage 'Deploy to stage-project'
	input 'Deploy to stage-project?'
	sh 'customDeployImageCommand'
	sh 'customDeployImageCommand with arguments'
  
	stage 'Deploy to prod-project'
	input 'Deploy to prod-project?'
	sh 'customDeployImageCommand'
	sh 'customDeployImageCommand with arguments'
}
