package tgcfs.Utils;

import tgcfs.Config.ReadConfig;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * Created by Alessandro Zonta on 26/09/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LogSystem {
    private final Logger logger;

    /**
     * Initialise the log
     * @throws IOException
     */
    public LogSystem(Class c, String name, String experiment, String path) throws Exception {
        this.logger = Logger.getLogger(c.getName()); //logger for this class

        int level = ReadConfig.Configurations.getDebugLevel();
        switch (level){
            case 0:
                this.logger.setLevel(Level.SEVERE);
                break;
            case 1:
                this.logger.setLevel(Level.WARNING);
                break;
            case 2:
                this.logger.setLevel(Level.INFO);
                break;
            case 3:
                this.logger.setLevel(Level.CONFIG);
                break;
            case 4:
                this.logger.setLevel(Level.FINE);
                break;
            case 5:
                this.logger.setLevel(Level.FINER);
                break;
            case 6:
                this.logger.setLevel(Level.FINEST);
                break;
            case 7:
                this.logger.setLevel(Level.OFF);
                break;
            default:
                this.logger.setLevel(Level.SEVERE);
        }

        Handler consoleHandler = new ConsoleHandler();

        String finalPath = path + "/Experiment-" + name;
        new File(finalPath).mkdirs();
        finalPath += "/" + experiment;
        new File(finalPath).mkdirs();
        finalPath += "/classifier.log";

        Handler fileHandler  = new FileHandler(finalPath);
        // Setting formatter to the handler
        // Creating SimpleFormatter
        Formatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
        this.logger.addHandler(consoleHandler);
        this.logger.addHandler(fileHandler);

        this.logger.removeHandler(consoleHandler);
    }

    /**
     * Getter for logger
     * @return {@link Logger} instance
     */
    public Logger getLogger() {
        return this.logger;
    }
}
