module.exports = {
	moduleConfig: {
		rootDirectory: process.cwd(),
		protractorConfig: 'protractor.conf'	,
		recordMode:true
	},
	mocksConfig: {
		dir: 'mocks',
		defaults: []
	}
};