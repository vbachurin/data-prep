var testConfig = {
    // *optional* babel options: isparta will use it as well as babel-loader
    babel: {
        presets: ['es2015']
    },
    // *optional* isparta options: istanbul behind isparta will use it
    isparta: {
        embedSource: true,
        noAutoWrap: true,
        // these babel options will be passed only to isparta and not to babel-loader
        babel: {
            presets: ['es2015']
        }
    },
    module: {
        devtool: 'inline-source-map',
        preLoaders: [
            // transpile all files except testing sources with babel as usual
            {
                test: /\.js$/,
                exclude: [
                    /node_modules/,
                    /\.spec\.js$/
                ],
                loader: 'isparta'
            }
        ],
        loaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loaders: ['ng-annotate', 'babel-loader']
            }
        ]
    },
    webpackMiddleware: {
        stats: {
            chunks: false
        }
    }
};

module.exports = testConfig;