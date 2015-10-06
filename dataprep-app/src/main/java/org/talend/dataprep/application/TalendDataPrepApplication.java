package org.talend.dataprep.application;


import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.talend.dataprep.application.os.LinuxConfiguration;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@SpringBootApplication
@Profile("bundled")
@ComponentScan(basePackages = "org.talend.dataprep")
public class TalendDataPrepApplication extends Application {

    public static final Logger LOGGER = LoggerFactory.getLogger(TalendDataPrepApplication.class);
    private static String url;
    private static ConfigurableApplicationContext context;
    private static SpringApplication application;
    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private WebView webView;
    private Stage primaryStage;
    private Stage mainStage;
    private static final int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;
    private Image iconApplication = new Image("/images/dataprep_splash.bmp");

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private void initStage() {
        ImageView splash = new ImageView(new Image("https://placeholdit.imgix.net/~text?txtsize=38&txt=Talend%20Data%20Preparation&w=676&h=227"));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH);
        progressText = new Label("Initialization . . .");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle("-fx-padding: 5; -fx-background-color: cornsilk; -fx-border-width:5; -fx-border-color: linear-gradient(to bottom, chocolate, derive(chocolate, 50%));");
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        initStage();
        showSplash();
        showMainStage();
    }

    private void showMainStage() {
        mainStage = new Stage(StageStyle.DECORATED);
        mainStage.setTitle("Talend Data Preparation");
        mainStage.setIconified(true);

        DataPrepApp dp = new DataPrepApp();

        dp.runningProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean wasRunning, Boolean isRunning) {
                if (!isRunning) {
                    // log information about current configuration
                    final ConfigurableEnvironment environment = context.getEnvironment();
                    LOGGER.debug("Dataset location: {}.", environment.getProperty("dataset.service.url"));
                    LOGGER.debug("Transformation location: {}.", environment.getProperty("transformation.service.url"));
                    LOGGER.debug("Preparation location: {}.", environment.getProperty("preparation.service.url"));
                    LOGGER.debug("Metadata location: {}.", environment.getProperty("dataset.metadata.store.file.location"));
                    LOGGER.debug("Content location: {}.", environment.getProperty("dataset.content.store.file.location"));
                    LOGGER.debug("Store location: {}.", environment.getProperty("preparation.store.file.location"));
                    LOGGER.debug("User data location: {}.", environment.getProperty("user.data.store.file.location"));
                    url = "http://127.0.0.1:" + environment.getProperty("server.port") + "/ui/index.html";
                    LOGGER.info("Talend Data Preparation started @ {}.", url);

                    VBox vBox = new VBox();
                    vBox.setSpacing(5);
                    // create buttons
                    Button btn = new Button("Open Talend Data Preparation");
                    btn.setAlignment(Pos.CENTER);

                    btn.setOnAction(
                            new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    context.publishEvent(new EmbeddedServletContainerInitializedEvent((EmbeddedWebApplicationContext) context, ((EmbeddedWebApplicationContext) context).getEmbeddedServletContainer()));
                                }
                            }
                    );

                    vBox.getChildren().add(btn);
                    vBox.getChildren().add(new Button("Call your favorite sales rep"));
                    vBox.getChildren().add(new Button("Subscribe to the community"));
                    vBox.getChildren().add(new Button("Vote to change this ugly button !"));
                    vBox.setAlignment(Pos.CENTER);

                    Scene scene = new Scene(vBox, 300, 250);
                    mainStage.setScene(scene);
                    mainStage.setIconified(false);
                    mainStage.getIcons().add(iconApplication);
                    mainStage.show();
                    primaryStage.toFront();
                    FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                    fadeSplash.setFromValue(1.0);
                    fadeSplash.setToValue(0.0);
                    fadeSplash.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            primaryStage.hide();
                        }
                    });
                    fadeSplash.play();
                }
            }
        });

        final Thread thread = new Thread(dp,"Talend-Data-Preparation");
        thread.setDaemon(true);
        thread.start();

    }

    private void showSplash() {
        Scene splashScene = new Scene(splashLayout);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(iconApplication);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        primaryStage.setScene(splashScene);
        primaryStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        primaryStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    private class DataPrepApp extends Task<String> {

        @Override
        protected String call() throws Exception {
            application = new SpringApplication(TalendDataPrepApplication.class);

            application.addListeners(new ApplicationListener<ApplicationEvent>() {
                @Override
                public void onApplicationEvent(ApplicationEvent applicationEvent) {
                    LOGGER.info(applicationEvent.toString());
                    if (applicationEvent instanceof ApplicationStartedEvent) {
                        updateProgress(0.1,"Initialization . . .");
                    } else if (applicationEvent instanceof ApplicationEnvironmentPreparedEvent) {
                        updateProgress(0.25,"Environment Ready");
                    } else if (applicationEvent instanceof ApplicationPreparedEvent) {
                        updateProgress(0.50,"Talend Data Preparation is starting, the force is coming !!!");
                    } else if (applicationEvent instanceof EmbeddedServletContainerInitializedEvent) {
                        updateProgress(1.0,"Let's rock !!!");
                    }
                }
            });
            LOGGER.info("Starting Talend Data Preparation...");
            context = application.run();

            return url;
        }


        private void updateProgress(final Double progress,final String text){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadProgress.setProgress(progress);
                     progressText.setText(text);
                }
            });
        }
    }
}


