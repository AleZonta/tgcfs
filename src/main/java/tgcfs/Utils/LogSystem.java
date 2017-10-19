package tgcfs.Utils;

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
    public LogSystem(Class c) throws IOException {
        this.logger = Logger.getLogger(c.getName()); //logger for this class
        Handler consoleHandler = new ConsoleHandler();
        Handler fileHandler  = new FileHandler("./classifier.log");
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
