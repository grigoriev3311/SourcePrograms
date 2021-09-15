package com.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WindowTimer {

    private final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
    private final SimpleDateFormat formatTimeMillis = new SimpleDateFormat("S");
    private final int windowWidth = 370;
    private final int windowHeight = 395;

    private boolean startTimer = false;

    private Calendar timerAllDate;
    private Calendar timerPeriodDate;

    private long startTime;
    private long timeForPeriod;
    private int waitTimerSecond;
    private Integer periodTimerSecond = 0;
    private Integer levelSound = 0;
    private int currentPeriod = -1;
    private ArrayList<Integer> arrayIntervalList = new ArrayList<Integer>(20);

    private JFrame frameTimer;

    private JLabel timerPeriodText;
    private JLabel timerAllText;
    private JLabel waitText;
    private JLabel intervalText;
    private JLabel valueText;
    private JLabel soundText;
    private JLabel secondText1;
    private JLabel secondText2;
    private JLabel intervalList;
    private JLabel currentInterval = new JLabel();

    private JLabel timerPeriod = new JLabel();
    private JLabel timerPeriodMillis = new JLabel();
    private JLabel timerAll  = new JLabel();
    private JLabel timerAllMillis = new JLabel();

    private JTextField periodTimer;
    private JTextField waitTimer;
    private JTextField soundTimer;

    private JPanel mainPanel;

    private JPanel panelValueUnion;
    private JPanel panelValueText;
    private JPanel panelWait;
    private JPanel panelInterval;
    private JPanel panelIntervalList;
    private JPanel panelSound;

    private JPanel panelPeriodUnion;
    private JPanel panelPeriodText;
    private JPanel panelPeriod;
    private JPanel panelCurrentPeriod;

    private JPanel panelAllUnion;
    private JPanel panelAllText;
    private JPanel panelAll;

    private Image iconTitle;
    private Icon iconRefresh;
    private Icon iconStart;
    private Icon iconStop;
    private Icon iconPlus;
    private Icon iconMinus;
    private Icon iconRight;
    private Icon iconSound;
    private Icon iconClear;

    private JButton buttonRefreshPeriod = new JButton();
    private JButton buttonRefreshAll = new JButton();
    private JButton buttonStartStop = new JButton();
    private JButton buttonPlus = new JButton();
    private JButton buttonMinus = new JButton();
    private JButton buttonCheckSound = new JButton();
    private JButton buttonAddInterval = new JButton();
    private JButton buttonClearInterval = new JButton();

    private Thread runSetterThread;

    private class SetterThread implements Runnable{
        @Override
        public void run(){
            IncrementTimer();
        }
    }

    private class RunSound implements Runnable{

        private Integer duration;
        private Integer valueLevel;

        RunSound(Integer duration, Integer valueLevel){
            this.valueLevel = valueLevel;
            this.duration = duration;
        }

        @Override
        public void run(){
            try {
                if(valueLevel > 0) {
                    SoundUtil.tone(15000, duration, valueLevel);
                }
            }
            catch (Exception ex){
            }
        }

    }

    public WindowTimer(){

        // Иконки
        try {
            iconTitle = ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\timer.png"));
            iconRefresh = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\refresh.png")));
            iconStart = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\start.png")));
            iconStop = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\stop.png")));
            iconPlus = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\up.png")));
            iconMinus = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\down.png")));
            iconRight = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\right.png")));
            iconSound = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\sound.png")));
            iconClear = new ImageIcon(ImageIO.read(new File("D:\\Icons\\TimerOfSwing\\clear.png")));
        }catch(Exception ex) {
            ShowError(frameTimer, "Ошибка открытия иконок для кнопок. " + ex.toString());
        }

        // Расположение, размеры и свойства окна
        frameTimer = new JFrame("Таймер");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frameTimer.setLocation((int)((dim.getWidth() - windowWidth)/2), (int)((dim.getHeight() - windowHeight)/2));
        frameTimer.setSize(windowWidth, windowHeight);
        frameTimer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameTimer.setResizable(false);
        frameTimer.setIconImage(iconTitle);

        // Установка начальных значений переменных счетчиков
        setTimerAllStartValue();
        setTimerPeriodStartValue();

        // Установка значений полей счетчиков
        SetTimerAll();

        buttonAddInterval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Integer valueInterval;

                try {
                    valueInterval = Integer.parseInt(periodTimer.getText());
                    if(valueInterval > 0){
                        arrayIntervalList.add(valueInterval);
                        PrintIntervalList();
                    }
                    if(currentPeriod == -1){
                        currentPeriod = 0;
                        periodTimerSecond = arrayIntervalList.get(0);
                    }
                }
                catch(NumberFormatException ex){
                    ShowError(frameTimer, "Некорректное значение уровня звука: "  + ex.toString());
                }
                catch(Exception ex){
                    ShowError(frameTimer, "Необработанное исключение: "  + ex.toString());
                }

            }
        });

        buttonClearInterval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                arrayIntervalList.clear();
                periodTimer.setText("0");
                PrintIntervalList();
                setTimerPeriodStartValue();
                SetTimerPeriod();
                timeForPeriod = System.currentTimeMillis();
                currentInterval.setText("Текущий интервал отсутствует");
                currentPeriod = -1;
                periodTimerSecond = 0;
            }
        });

        buttonPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Integer valueSound;

                try {
                    valueSound = Integer.parseInt(soundTimer.getText());
                    valueSound = valueSound < 1 ? valueSound + 1 : valueSound;
                    soundTimer.setText(valueSound.toString());
                    levelSound = valueSound;
                }
                catch(NumberFormatException ex){
                    ShowError(frameTimer, "Некорректное значение уровня звука: "  + ex.toString());
                }
                catch(Exception ex){
                    ShowError(frameTimer, "Необработанное исключение: "  + ex.toString());
                }

            }
        });

        buttonMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Integer valueSound;

                try {
                    valueSound = Integer.parseInt(soundTimer.getText());
                    valueSound = valueSound > 0 ? valueSound - 1 : valueSound;
                    soundTimer.setText(valueSound.toString());
                    levelSound = valueSound;
                }
                catch(NumberFormatException ex){
                    ShowError(frameTimer, "Некорректное значение уровня звука: "  + ex.toString());
                }
                catch(Exception ex){
                    ShowError(frameTimer, "Необработанное исключение: "  + ex.toString());
                }

            }
        });

        buttonCheckSound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sound(100);
            }
        });

        buttonRefreshAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Установка начальных значений переменных счетчиков
                setTimerAllStartValue();
                setTimerPeriodStartValue();
                // Установка значений полей счетчиков
                SetTimerAll();
                // Начальный интервал
                if(currentPeriod >= 0){
                    currentPeriod = 0;
                    periodTimerSecond = arrayIntervalList.get(0);
                }
                currentInterval.setText("Текущий интервал отсутствует");
                startTime = System.currentTimeMillis();
                timeForPeriod = System.currentTimeMillis();
            }
        });

        buttonRefreshPeriod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Установка начальных значений переменных счетчиков
                setTimerPeriodStartValue();
                // Установка значений полей счетчиков
                SetTimerPeriod();
                // Начальный интервал
                if(currentPeriod >= 0){
                    currentPeriod = 0;
                    periodTimerSecond = arrayIntervalList.get(0);
                }
                currentInterval.setText("Текущий интервал отсутствует");
                timeForPeriod = System.currentTimeMillis();
            }
        });

        buttonStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startTimer){
                    StopTimer();
                }else
                {
                    try {
                        //Получение значения ожидания
                        waitTimerSecond = Integer.parseInt(waitTimer.getText());
                        runSetterThread = new Thread(new SetterThread());
                        StartTimer();
                        // Фиксация начальной точки времени
                        // Прибавляем ожидание
                        startTime = System.currentTimeMillis() + waitTimerSecond*1000;
                        timeForPeriod = System.currentTimeMillis() + waitTimerSecond*1000;
                        // Запуск отдельного процесса таймера
                        runSetterThread.start();
                    }
                    catch(NumberFormatException ex){
                        StopTimer();
                        ShowError(frameTimer, "Некорректное значение ожидания: "  + ex.toString());
                    }
                    catch(Exception ex){
                        StopTimer();
                        ShowError(frameTimer, "Необработанное исключение: "  + ex.toString());
                    }
                }
            }
        });

        periodTimer = new JTextField("0");
        waitTimer = new JTextField("0");
        soundTimer = new JTextField("0");

        // Формирование GUI
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException ex) {
            ShowError(frameTimer, "Ошибка при установки внешнего вида: " + ex.toString());
        } catch (Exception ex) {
            ShowError(frameTimer, "Ошибка при установки внешнего вида: " + ex.toString());
        }

        // Размеры и шрифты
        Dimension dimensionPanelTimerText = new Dimension(windowWidth, 20);
        Dimension dimensionTimerText = new Dimension(200, 20);
        Font fontTimerText = new Font(null, Font.BOLD, 16);

        Dimension dimensionPanelTimer = new Dimension(windowWidth, 60);
        Dimension dimensionTimer = new Dimension(200, 60);
        Font fontTimer = new Font(null, Font.PLAIN, 50);

        Dimension dimensionTimerMills = new Dimension(20, 43);
        Font fontTimerMIlls = new Font(null, Font.PLAIN, 20);

        Dimension dimensionButton = new Dimension(40, 40);

        Dimension dimensionPanelValue = new Dimension(windowWidth, 20);
        Dimension dimensionValueText = new Dimension(100, 20);

        Font fontValue = new Font(null, Font.ITALIC, 12);

        Dimension dimensionPanelUnionTimer = new Dimension(windowWidth, 95);

        // Панель с заголовком для панели параметров
        panelValueText = CreatePanel(BoxLayout.X_AXIS, dimensionPanelTimerText);
        valueText = new JLabel("Параметры");
        panelValueText.add(Box.createHorizontalStrut(5));
        SetPropertyLabel(panelValueText, valueText, JLabel.LEFT, JLabel.BOTTOM, dimensionTimerText, fontTimerText, new Color(0, 0, 180));

        // Панель с задержкой
        panelWait = CreatePanel(BoxLayout.X_AXIS, dimensionPanelValue);
        panelWait.add(Box.createHorizontalStrut(5));
        waitText = new JLabel("Задержка старта");
        SetPropertyLabel(panelWait, waitText, JLabel.LEFT, JLabel.CENTER, new Dimension(110, 20), fontValue, Color.BLACK);
        SetPropertyTextField(panelWait, waitTimer, JTextField.CENTER, new Dimension(30, 20), fontValue, Color.BLACK);
        secondText1 = new JLabel("секунд");
        panelWait.add(Box.createHorizontalStrut(5));
        SetPropertyLabel(panelWait, secondText1, JLabel.LEFT, JLabel.CENTER, new Dimension(50, 20), fontValue, Color.BLACK);

        // Панель с интервалом
        panelInterval = CreatePanel(BoxLayout.X_AXIS, dimensionPanelValue);
        panelInterval.add(Box.createHorizontalStrut(5));
        intervalText = new JLabel("Интервал");
        SetPropertyLabel(panelInterval, intervalText, JLabel.LEFT, JLabel.CENTER, new Dimension(65, 20), fontValue, Color.BLACK);
        SetPropertyTextField(panelInterval, periodTimer, JTextField.CENTER, new Dimension(30, 20), fontValue, Color.BLACK);
        secondText2 = new JLabel("секунд");
        panelInterval.add(Box.createHorizontalStrut(5));
        SetPropertyLabel(panelInterval, secondText2, JLabel.LEFT, JLabel.CENTER, new Dimension(45, 20), fontValue, Color.BLACK);
        SetPropertyButton(panelInterval, buttonAddInterval, JLabel.CENTER, JLabel.CENTER, new Dimension(20, 20), Color.BLACK, iconRight, "Добавить интервал в список");
        panelInterval.add(Box.createHorizontalStrut(5));
        SetPropertyButton(panelInterval, buttonClearInterval, JLabel.CENTER, JLabel.CENTER, new Dimension(20, 20), Color.BLACK, iconClear, "Очистить список");

        // Панель со списком интервалов
        panelIntervalList = CreatePanel(BoxLayout.X_AXIS, dimensionPanelValue);
        panelIntervalList.add(Box.createHorizontalStrut(5));
        intervalList = new JLabel("Список интервалов пуст");
        SetPropertyLabel(panelIntervalList, intervalList, JLabel.LEFT, JLabel.CENTER, new Dimension(windowWidth, 20), new Font(null, Font.BOLD, 12), Color.BLACK);

        // Панель с настрокой звука
        panelSound = CreatePanel(BoxLayout.X_AXIS, dimensionPanelValue);
        panelSound.add(Box.createHorizontalStrut(5));
        soundText = new JLabel("Горомкость звука");
        SetPropertyLabel(panelSound, soundText, JLabel.LEFT, JLabel.CENTER, new Dimension(110, 20), fontValue, Color.BLACK);
        panelSound.add(Box.createHorizontalStrut(5));
        SetPropertyButton(panelSound, buttonPlus, JLabel.CENTER, JLabel.CENTER, new Dimension(20, 20), Color.BLACK, iconPlus, "Увеличить");
        panelSound.add(Box.createHorizontalStrut(5));
        SetPropertyTextField(panelSound, soundTimer, JTextField.CENTER, new Dimension(20, 20), fontValue, Color.BLACK);
        panelSound.add(Box.createHorizontalStrut(5));
        SetPropertyButton(panelSound, buttonMinus, JLabel.CENTER, JLabel.CENTER, new Dimension(20, 20), Color.BLACK, iconMinus, "Уменьшить");
        panelSound.add(Box.createHorizontalStrut(10));
        SetPropertyButton(panelSound, buttonCheckSound, JLabel.CENTER, JLabel.CENTER, new Dimension(20, 20), Color.BLACK, iconSound, "Проверить звук");

        // Объединенная панель с параметрами
        panelValueUnion = CreatePanel(BoxLayout.Y_AXIS, new Dimension(windowWidth, 125));
        panelValueUnion.add(Box.createVerticalStrut(5));
        panelValueUnion.add(panelValueText);
        panelValueUnion.add(Box.createVerticalStrut(5));
        panelValueUnion.add(panelWait);
        panelValueUnion.add(Box.createVerticalStrut(3));
        panelValueUnion.add(panelInterval);
        panelValueUnion.add(Box.createVerticalStrut(3));
        panelValueUnion.add(panelIntervalList);
        panelValueUnion.add(Box.createVerticalStrut(3));
        panelValueUnion.add(panelSound);
        panelValueUnion.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Панель с заголовком для панели общего времени
        panelAllText = CreatePanel(BoxLayout.X_AXIS, dimensionPanelTimerText);
        panelAllText.add(Box.createHorizontalStrut(5));
        timerAllText = new JLabel("Общий секундомер");
        SetPropertyLabel(panelAllText, timerAllText, JLabel.LEFT, JLabel.BOTTOM, dimensionTimerText, fontTimerText, new Color(0, 0, 180));

        // Панель с таймером общего времени
        panelAll = CreatePanel(BoxLayout.X_AXIS, dimensionPanelTimer);
        panelAll.add(Box.createHorizontalStrut(5));
        SetPropertyLabel(panelAll, timerAll, JLabel.LEFT, JLabel.BOTTOM, dimensionTimer, fontTimer, Color.BLACK);
        SetPropertyLabel(panelAll, timerAllMillis, JLabel.LEFT, JLabel.BOTTOM, dimensionTimerMills, fontTimerMIlls, Color.BLACK);
        panelAll.add(Box.createHorizontalStrut(25));
        SetPropertyButton(panelAll, buttonRefreshAll, JLabel.CENTER, JLabel.CENTER, dimensionButton, Color.BLACK, iconRefresh, "Обнулить счетчик");
        panelAll.add(Box.createHorizontalStrut(5));
        SetPropertyButton(panelAll, buttonStartStop, JLabel.CENTER, JLabel.CENTER, dimensionButton, Color.BLACK, iconStart, "Запуск");

        // Объединенная панель с таймером общего времени
        panelAllUnion = CreatePanel(BoxLayout.Y_AXIS, dimensionPanelUnionTimer);
        panelAllUnion.add(Box.createVerticalStrut(5));
        panelAllUnion.add(panelAllText);
        panelAllUnion.add(Box.createVerticalStrut(5));
        panelAllUnion.add(panelAll);
        panelAllUnion.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Панель с заголовком для панели интервалов
        panelPeriodText = CreatePanel(BoxLayout.X_AXIS, dimensionPanelTimerText);
        panelPeriodText.add(Box.createHorizontalStrut(5));
        timerPeriodText = new JLabel("Таймер интервалов");
        SetPropertyLabel(panelPeriodText, timerPeriodText, JLabel.LEFT, JLabel.BOTTOM, dimensionTimerText, fontTimerText, new Color(0, 0, 180));

        // Панель с таймером интервалов
        panelPeriod = CreatePanel(BoxLayout.X_AXIS, dimensionPanelTimer);
        panelPeriod.add(Box.createHorizontalStrut(5));
        SetPropertyLabel(panelPeriod, timerPeriod, JLabel.LEFT, JLabel.BOTTOM, dimensionTimer, fontTimer, Color.BLACK);
        SetPropertyLabel(panelPeriod, timerPeriodMillis, JLabel.LEFT, JLabel.BOTTOM, dimensionTimerMills, fontTimerMIlls, Color.BLACK);
        panelPeriod.add(Box.createHorizontalStrut(25));
        SetPropertyButton(panelPeriod, buttonRefreshPeriod, JLabel.CENTER, JLabel.CENTER, dimensionButton, Color.BLACK, iconRefresh, "Обнулить счетчик");

        // Панель с текущим интервалом
        panelCurrentPeriod = CreatePanel(BoxLayout.X_AXIS, new Dimension(windowWidth, 20));
        panelCurrentPeriod.add(Box.createHorizontalStrut(5));
        //currentInterval.setText("Текущий интервал отсутствует");
        SetPropertyLabel(panelCurrentPeriod, currentInterval, JLabel.LEFT, JLabel.BOTTOM, new Dimension(windowWidth, 20), new Font(null, Font.ITALIC, 12), Color.BLACK);

        // Объединенная панель с таймером общего времени
        panelPeriodUnion = CreatePanel(BoxLayout.Y_AXIS, new Dimension(windowWidth, 115));
        panelPeriodUnion.add(Box.createVerticalStrut(5));
        panelPeriodUnion.add(panelPeriodText);
        panelPeriodUnion.add(Box.createVerticalStrut(5));
        panelPeriodUnion.add(panelPeriod);
        panelPeriodUnion.add(panelCurrentPeriod);
        panelPeriodUnion.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panelValueUnion);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panelAllUnion);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panelPeriodUnion);

        frameTimer.add(mainPanel);

        frameTimer.setVisible(true);

    }

    // Запуск
    private void StartTimer(){
        startTimer = true;
        buttonStartStop.setIcon(iconStop);
        buttonStartStop.setToolTipText("Остановка");
        waitTimer.setEnabled(false);
        buttonAddInterval.setEnabled(false);
        periodTimer.setEnabled(false);
        buttonClearInterval.setEnabled(false);
        buttonPlus.setEnabled(false);
        soundTimer.setEnabled(false);
        buttonMinus.setEnabled(false);
        buttonRefreshAll.setEnabled(false);
        buttonRefreshPeriod.setEnabled(false);
    }

    // Остановка
    private void StopTimer(){
        startTimer = false;
        buttonStartStop.setIcon(iconStart);
        buttonStartStop.setToolTipText("Запуск");
        waitTimer.setEnabled(true);
        buttonAddInterval.setEnabled(true);
        periodTimer.setEnabled(true);
        buttonClearInterval.setEnabled(true);
        buttonPlus.setEnabled(true);
        soundTimer.setEnabled(true);
        buttonMinus.setEnabled(true);
        buttonRefreshAll.setEnabled(true);
        buttonRefreshPeriod.setEnabled(true);
    }

    // Функция для создания панелей
    private JPanel CreatePanel(int intBoxLayout, Dimension dimension){
        JPanel newPanel;
        newPanel = new JPanel();
        newPanel.setLayout(new BoxLayout(newPanel, intBoxLayout));
        newPanel.setPreferredSize(dimension);
        newPanel.setMinimumSize(dimension);
        newPanel.setMaximumSize(dimension);
        //newPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        return newPanel;
    }

    // Функция для установки свойств меток и добавления в панель
    private void SetPropertyLabel(JPanel jpanel, JLabel jlabel, int alignmentX, int alignmentY, Dimension dimension, Font font, Color color){
        jlabel.setHorizontalAlignment(alignmentX);
        jlabel.setVerticalAlignment(alignmentY);
        jlabel.setPreferredSize(dimension);
        jlabel.setMinimumSize(dimension);
        jlabel.setMaximumSize(dimension);
        jlabel.setFont(font);
        jlabel.setForeground(color);
        jpanel.add(jlabel);
        //jlabel.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    // Функция для установки свойств кнопок
    private void SetPropertyButton(JPanel jpanel, JButton jbutton, int alignmentX, int alignmentY, Dimension dimension, Color color, Icon icon, String tipTexp) {
        jbutton.setHorizontalAlignment(alignmentX);
        jbutton.setVerticalAlignment(alignmentY);
        jbutton.setPreferredSize(dimension);
        jbutton.setMinimumSize(dimension);
        jbutton.setMaximumSize(dimension);
        jbutton.setForeground(color);
        jbutton.setIcon(icon);
        jbutton.setToolTipText(tipTexp);
        jpanel.add(jbutton);
        //jbutton.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    // Функция для установки свойств кнопок
    private void SetPropertyTextField(JPanel jpanel, JTextField textField, int alignmentX, Dimension dimension, Font font, Color color) {
        textField.setHorizontalAlignment(alignmentX);
        textField.setPreferredSize(dimension);
        textField.setMinimumSize(dimension);
        textField.setMaximumSize(dimension);
        textField.setFont(font);
        textField.setForeground(color);
        jpanel.add(textField);
        //textField.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    // Вывод сообщения об ошибке
    private void ShowError(Component component, String message){
       JOptionPane.showConfirmDialog(component,
                                     message,
                                     "Ошибка",
                                     JOptionPane.DEFAULT_OPTION,
                                     JOptionPane.ERROR_MESSAGE);
    }

    // Установка счетчика периода в начальное положение / Обнуление счетчика
    private void setTimerPeriodStartValue(){
        timerPeriodDate = Calendar.getInstance();
        timerPeriodDate.set(Calendar.HOUR_OF_DAY, 0);
        timerPeriodDate.set(Calendar.MINUTE, 0);
        timerPeriodDate.set(Calendar.SECOND, 0);
        timerPeriodDate.set(Calendar.MILLISECOND, 0);
    }

    // Установка общего счетчика в начальное положение / Обнуление счетчика
    private void setTimerAllStartValue(){
        timerAllDate = Calendar.getInstance();
        timerAllDate.set(Calendar.HOUR_OF_DAY, 0);
        timerAllDate.set(Calendar.MINUTE, 0);
        timerAllDate.set(Calendar.SECOND, 0);
        timerAllDate.set(Calendar.MILLISECOND, 0);
    }

    // Установка значений таймера в поля
    private void SetTimerPeriod(){
        timerPeriod.setText(formatTime.format(timerPeriodDate.getTime()));
        timerPeriodMillis.setText("." + formatTimeMillis.format(timerPeriodDate.getTime()).substring(0, 1));
    }

    // Установка значений таймера в поля
    private void SetTimerAll(){
        SetTimerPeriod();
        timerAll.setText(formatTime.format(timerAllDate.getTime()));
        timerAllMillis.setText("." + formatTimeMillis.format(timerAllDate.getTime()).substring(0, 1));
    }

    // Установка информации о периодах
    private void SetPeriodInfo()
    {
        Integer nextPeriod = 0;
        if(currentPeriod >= 0){
            nextPeriod = currentPeriod + 1;
            if(nextPeriod >= arrayIntervalList.size()){
                nextPeriod = 0;
            }
            currentInterval.setText("Текущий интервал - " + arrayIntervalList.get(currentPeriod) + " секунд, следующий - " + arrayIntervalList.get(nextPeriod) + " секунд");
        }
    }

    // Установка списка интервалов
    private void PrintIntervalList(){
        int i = 0;
        for(i = 0; i < arrayIntervalList.size(); i++){
            if(i == 0){
                intervalList.setText("Список интервалов: ");
            }
            intervalList.setText(intervalList.getText() + arrayIntervalList.get(i) + "..");
        }
        if(i == 0){
            intervalList.setText("Список интервалов пуст");
        }
    }

    // Звуковой сигнал
    private void Sound(Integer duration){
        if(levelSound > 0) {
            Thread thread;
            thread = new Thread(new RunSound(duration, 1));
            thread.start();
        }
    }

    // Увеличение переменной счетчика
    private void IncrementTimer(){

        long currentTime;
        int duration;
        long wait = waitTimerSecond * 1000;
        long diffTimeSecondPeriod;
        int signal10 = 0;
        int signal4 = 0;
        int signal3 = 0;
        int signal2 = 0;
        int signal1 = 0;

        while(startTimer){

            try {
                // Ожидание перед запуском
                if(wait > 0) {
                    // Отсчет за 5 секунд до начала, если позволяет задержка старта
                    // Или только звуковой сигнал начала
                    if(wait - 4000 >= 0) {
                        Thread.sleep(wait - 4000);
                        for(int i=0; i<=3; i++){
                            Sound(100);
                            Thread.sleep(1000);
                        }
                    }else{
                        Thread.sleep(wait);
                    }
                    Sound(500);
                }
                wait = 0;
                diffTimeSecondPeriod = (System.currentTimeMillis() - timeForPeriod) / 1000;
                // Сигнал за 10 секунд до окончания интервала, если интервал позволяет
                if(periodTimerSecond >= 20){
                    if(signal10 == 0 && diffTimeSecondPeriod >= periodTimerSecond - 10 && diffTimeSecondPeriod < periodTimerSecond - 9) {Sound(100); signal10 = 1;};
                }
                // Отсчет за 5 секунд до окончания интервала, если интервал позволяет
                if(periodTimerSecond >= 10){
                    if(signal4 == 0 && diffTimeSecondPeriod >= periodTimerSecond - 4 && diffTimeSecondPeriod < periodTimerSecond - 3) {Sound(100); signal4 = 1;};
                    if(signal3 == 0 && diffTimeSecondPeriod >= periodTimerSecond - 3 && diffTimeSecondPeriod < periodTimerSecond - 2) {Sound(100); signal3 = 1;};
                    if(signal2 == 0 && diffTimeSecondPeriod >= periodTimerSecond - 2 && diffTimeSecondPeriod < periodTimerSecond - 1) {Sound(100); signal2 = 1;};
                    if(signal1 == 0 && diffTimeSecondPeriod >= periodTimerSecond - 1 && diffTimeSecondPeriod < periodTimerSecond) {Sound(100); signal1 = 1;};
                }
                // Обнуляем счетчик периода, если он достигнут, для расчета сначала
                // Берем следующее значение из коллекции периодов или начинаем обработка этой коллекции сначала
                if(periodTimerSecond > 0 && diffTimeSecondPeriod >= periodTimerSecond){
                    Sound(500);
                    setTimerPeriodStartValue();
                    timeForPeriod = System.currentTimeMillis();
                    currentPeriod++;
                    if(currentPeriod >= arrayIntervalList.size()){
                        currentPeriod = 0;
                    }
                    periodTimerSecond = arrayIntervalList.get(currentPeriod);
                    signal10 = 0; signal4 = 0; signal3 = 0; signal2 = 0; signal1 = 0;
                }
                // Приостанавливаем, чтобы не блокировать GUI
                Thread.sleep(100);
                // Расчет смещения счетчика вперед. Сколько милисекунд прошло с последнего смещения
                currentTime = System.currentTimeMillis();
                duration = (int)(currentTime - startTime);
                startTime = currentTime;
                // Добавления смещения в переменные счетчиков
                timerAllDate.add(Calendar.MILLISECOND, duration);
                if(periodTimerSecond > 0){
                    timerPeriodDate.add(Calendar.MILLISECOND, duration);
                }
                // Обновление значений в GUI через очередь сообщений
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        SetTimerAll();
                        SetPeriodInfo();
                    }
                });
            }
            catch(Exception ex){
                StopTimer();
                ShowError(frameTimer, "Необработанное исключение: "  + ex.toString());
            }
        }

    }

}