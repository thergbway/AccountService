package Server;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

class ServerLogger {
    private static ServerLogger instance;
    private Logger logger;

    private ServerLogger(){
        logger = Logger.getLogger(ServerLogger.class.getName());
        try {
            HtmlFormatter htmlformatter = new HtmlFormatter();
            FileHandler htmlfile = new FileHandler("ServerLog.html");
            // Устанавливаем html форматирование с помощью класса HtmlFormatter.
            htmlfile.setFormatter(htmlformatter);
            logger.addHandler(htmlfile);
        } catch (SecurityException e) {
            logger.log(Level.SEVERE,
                    "Не удалось создать файл лога из-за политики безопасности.",
                    e);
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Не удалось создать файл лога из-за ошибки ввода-вывода.",
                    e);
        }
    }
    public static ServerLogger getInstance(){
        if(instance == null)
            instance= new ServerLogger();
        return instance;
    }
    public void log(Level lev, String str, Throwable thr){
        logger.log(lev, str, thr);
    }
    public void info(String msg){
        logger.info(msg);
    }
    public void warning(String msg){
        logger.warning(msg);
    }
    public void severe(String msg){
        logger.severe(msg);
    }
    public void addHandler(Handler handler){
        try{
            logger.addHandler(handler);
        }
        catch (SecurityException e){
            logger.log(Level.SEVERE,
                    "Не удалось создать файл лога из-за политики безопасности.",
                    e);
        }
    }
    public void close(){
        for(Handler handler: logger.getHandlers())
            handler.close();
    }
}

class HtmlFormatter extends Formatter {
    public HtmlFormatter(){}

    //Возвращаем заголовочную часть HTML файла.
    @Override
    public String getHead(Handler h)
    {
        //Записываем заголовок HTML файла, мета информацию и начало таблицы
        return "<html><head><title>Server Log</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
                "</head><body>" +
                "<table border=1>" +
                "<tr bgcolor=CYAN><td>date</td><td>level</td>" +
                "<td>message</td><td>thrown message</td><td>stacktrace</td></tr>";
    }

    //Возвращаем конец HTML файла
    @Override
    public String getTail(Handler h)
    {
        //Записываем окончание таблицы и конец HTML файла
        return "</table></body></html>";
    }

    //Форматируем одно сообщение в строку таблицы
    @Override
    public String format(LogRecord record)
    {
        StringBuilder result=new StringBuilder();
        Date d = new Date();
        Level level = record.getLevel();

        /**
         * Ошибки будут выделены красным цветом,
         * предупреждения - серым,
         * информационные сообщения - белым.
         */
        if (level==Level.SEVERE)
        {
            result.append("<tr bgColor=Tomato><td>");
        }
        else if (level==Level.WARNING)
        {
            result.append("<tr bgColor=GRAY><td>");
        }
        else//INFO
        {
            result.append("<tr bgColor=WHITE><td>");
        }

        result.append("\n");


        result.append(d);
        result.append("</td><td>");
        result.append(record.getLevel().toString());
        result.append("</td><td>");
        result.append(record.getMessage());
        result.append("</td><td>");



        Throwable thrown = record.getThrown();



        if (thrown!=null)
        {
            // Если было передано исключение, то выводим полный
            // стек вызовов.
            result.append(record.getThrown().getMessage());
            result.append("</td><td>");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            String stackTrace=sw.toString();

            result.append(stackTrace);
            result.append("</td>");
        }
        else
        {
            // Просто пустые ячейки.
            result.append("</td><td>null");
            result.append("</td>");
        }

        // Конец строки
        result.append("</tr>\n");
        return result.toString();
    }
}

class JTextAreaHandler extends Handler{
    private JTextArea jTextArea;

    public JTextAreaHandler(JTextArea jTextArea){
        this.jTextArea= jTextArea;
        setFormatter(new SimpleFormatter());
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        String msg;
        try {
            msg = getFormatter().format(record);
            jTextArea.append("\n>> " + msg);
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
    }

    @Override
    public synchronized void flush() {}

    @Override
    public synchronized void close() throws SecurityException {}
}