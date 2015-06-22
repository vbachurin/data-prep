function startTourIntro(){
  var introTourItems = introJs();
    introTourItems.setOptions({
    steps: [
        {
          element: '.no-js',
          title: 'Welcome to Talend Data Preparation!',
          intro: 'To know more about Talend Data Preparation, take this quick tour!',
          position: 'right'
        },
        {
          element: '#help-import-local',
          title: 'Importing files',
          intro: 'Click here to import a new local file (csv or Excel).',
          position: 'right'
        },
        {
            element: '#nav_home_datasets',
            title: 'Browsing datasets',
            intro: 'Here you can browse through the datasets you imported.<br/>Datasets correspond to the files you want to work on.',
            position: 'right'
        },
        {
            element: '#nav_home_preparations',
            title: 'Browsing preparations',
            intro: 'Here you can browse through the preparations you made for your datasets.<br/>Preparations are transformations you apply on a file to clean it.',
            position: 'right'
        },
        {
            element: '#home_first_file',
            title: 'Opening a file',
            intro: 'Now open a file to get started with Data Prep and create your first preparation!',
            position: 'right'
        }
    ]
  });

    introTourItems.start();
}
