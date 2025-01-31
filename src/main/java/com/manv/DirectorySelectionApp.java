package com.manv;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DirectorySelectionApp {

    private JTextField inputField;
    private JTextField outputField;

    public static void main(String[] args) {
        // Создаем окно приложения
        JFrame frame = new JFrame("Directory Selection App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 200);

        // Создаем панель для размещения компонентов
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Панель для кнопок и текстовых полей
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 1));

        // Панель для отображения выбраных директорий
        JPanel pathsPanel = new JPanel();
        pathsPanel.setLayout(new GridLayout(3, 1));

        // Создаем экземпляр приложения
        DirectorySelectionApp app = new DirectorySelectionApp();

        // Кнопка для выбора входной директории
        JButton inputButton = new JButton("Select Input Directory");
        inputButton.addActionListener(app.new InputDirectoryListener());
        buttonsPanel.add(inputButton);

        // Поле для отображения выбранной входной директории
        app.inputField = new JTextField();
        app.inputField.setEditable(false);
        pathsPanel.add(app.inputField);

        // Кнопка для выбора выходной директории
        JButton outputButton = new JButton("Select Output Directory");
        outputButton.addActionListener(app.new OutputDirectoryListener());
        buttonsPanel.add(outputButton);

        // Поле для отображения выбранной выходной директории
        app.outputField = new JTextField();
        app.outputField.setEditable(false);
        pathsPanel.add(app.outputField);

        // Кнопка для запуска метода
        JButton startButton = new JButton("Start");
        startButton.addActionListener(app.new StartActionListener());
        buttonsPanel.add(startButton);

        // Добавляем пустое текстовое поле для выравнивания
        pathsPanel.add(new JLabel());

        // Добавляем панели в основную панель
        panel.add(buttonsPanel, BorderLayout.WEST);
        panel.add(pathsPanel, BorderLayout.CENTER);

        // Добавляем панель в окно
        frame.add(panel);

        // Отображаем окно
        frame.setVisible(true);
    }

    // Метод для выбора директории через проводник
    private String chooseDirectory(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    // Внутренний класс для обработки нажатия на кнопку выбора входной директории
    private class InputDirectoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedDirectory = chooseDirectory("Select Input Directory");
            if (selectedDirectory != null) {
                inputField.setText(selectedDirectory);
            }
        }
    }

    // Внутренний класс для обработки нажатия на кнопку выбора выходной директории
    private class OutputDirectoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedDirectory = chooseDirectory("Select Output Directory");
            if (selectedDirectory != null) {
                outputField.setText(selectedDirectory);
            }
        }
    }

    // Внутренний класс для обработки нажатия на кнопку "Start"
    private class StartActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String inputPath = inputField.getText();
            String outputPath = outputField.getText();

            if (inputPath.isEmpty() || outputPath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select both directories", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int fileCount = writeAllFileNames(inputPath, outputPath);
                JOptionPane.showMessageDialog(null, "Files processed successfully! Total files: " + fileCount, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // Рекурсивный метод для обхода всех файлов и директорий
        private void traverseFiles(File directory, FileWriter writer, Counter counter) throws IOException {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        traverseFiles(file, writer, counter); // Рекурсивный вызов для поддиректорий
                    } else {
                        writer.write(file.getName() + System.lineSeparator()); // Запись имени файла
                        counter.increment(); // Увеличиваем счетчик файлов
                    }
                }
            }
        }

        // Метод для записи всех имен файлов из входной директории в выходной файл и подсчет файлов
        private int writeAllFileNames(String inputPath, String outputPath) {
            File inputDir = new File(inputPath);
            File outputDir = new File(outputPath);

            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String fileName = inputDir.getName() + ".txt";
            File outputFile = new File(outputDir, fileName);

            Counter counter = new Counter(); // Инициализируем счетчик

            try (FileWriter writer = new FileWriter(outputFile)) {
                traverseFiles(inputDir, writer, counter);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "An error occurred while processing files: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            return counter.getCount(); // Возвращаем количество файлов
        }
    }

    // Вспомогательный класс для подсчета файлов
    private static class Counter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }
}