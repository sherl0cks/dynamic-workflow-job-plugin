OpenShiftClient oc = new com.rhc.automation.clients.OpenShiftClient()
DockerClient docker = new com.rhc.automation.clients.DockerClient()

node {
	stage 'Code Checkout'
	checkout scm
	
	stage 'Build App'
	oc.login( 'master.openshift.redhat.com' )
	docker.login('registry.apps.redhat.com', oc.getTrimmedUserToken() )
	
	echo 'Using build tool: mvn-3'
	def toolHome = tool 'mvn-3'
	sh "${toolHome}/bin/mvn clean deploy"
	
	stage 'Build Image and Deploy to Dev'
	echo 'No buildImageCommands, using default OpenShift image build and deploy'
	oc.startBuildAndWaitUntilComplete( 'cool-application-name', 'dev-project' )
}
