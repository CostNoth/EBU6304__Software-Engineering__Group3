import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import util.DeepSeekUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainFrame extends JFrame {
    private JTextField amountField, descriptionField, categoryField;
    private JComboBox<String> typeComboBox;
    private JButton saveButton, importButton, classifyButton, insightButton, resetButton;
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private String filePath = "src/records.txt";
    private int selectedRow = -1; // 用于保存选中的行索引

    public MainFrame() {
        setTitle("智能个人理财管理器");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置整体布局
        setLayout(new BorderLayout());

        // 表格展示区域
        tableModel = new DefaultTableModel(new Object[]{"类型", "金额", "用途", "分类", "时间"}, 0);
        recordsTable = new JTable(tableModel);
        recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recordsTable);
        add(scrollPane, BorderLayout.CENTER);

        // 操作区域
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        panel.add(new JLabel("金额:"));
        amountField = new JTextField();
        panel.add(amountField);

        panel.add(new JLabel("用途:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);

        panel.add(new JLabel("分类:"));
        categoryField = new JTextField();
        panel.add(categoryField);

        panel.add(new JLabel("类型:"));
        typeComboBox = new JComboBox<>(new String[]{"支出", "收入"});
        panel.add(typeComboBox);

        saveButton = new JButton("保存记录");
        importButton = new JButton("导入CSV");
        classifyButton = new JButton("费用分类");
        insightButton = new JButton("洞察与预测");
        resetButton = new JButton("重置");

        panel.add(saveButton);
        panel.add(importButton);
        panel.add(classifyButton);
        panel.add(insightButton);
        panel.add(resetButton);

        add(panel, BorderLayout.NORTH);

        // 保存记录按钮
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveRecord();
            }
        });

        // 导入CSV按钮
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importCSV();
            }
        });

        // 费用分类按钮
        classifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                classifyExpenses();
            }
        });

        // 洞察与预测按钮
        insightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showInsights();
            }
        });

        // 重置按钮
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        // 监听表格行选择事件，回填数据到表单
        recordsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && recordsTable.getSelectedRow() != -1) {
                selectedRow = recordsTable.getSelectedRow();
                String type = (String) tableModel.getValueAt(selectedRow, 0);
                String amount = (String) tableModel.getValueAt(selectedRow, 1);
                String description = (String) tableModel.getValueAt(selectedRow, 2);
                String category = (String) tableModel.getValueAt(selectedRow, 3);

                // 填充表单
                amountField.setText(amount);
                descriptionField.setText(description);
                categoryField.setText(category);
                typeComboBox.setSelectedItem(type);
            }
        });

        // 加载现有记录
        loadRecords();
        setVisible(true);
    }

    private void saveRecord() {
        String amount = amountField.getText();
        String description = descriptionField.getText();
        String category = categoryField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String record = type + "," + amount + "," + description + "," + category + "," + time;

        if (selectedRow == -1) { // 新增记录
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(record);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tableModel.addRow(new String[]{type, amount, description, category, time});
            JOptionPane.showMessageDialog(this, "记录已保存!");
        } else { // 更新记录
            tableModel.setValueAt(type, selectedRow, 0);
            tableModel.setValueAt(amount, selectedRow, 1);
            tableModel.setValueAt(description, selectedRow, 2);
            tableModel.setValueAt(category, selectedRow, 3);

            // 更新文件中的记录
            updateFile();
            JOptionPane.showMessageDialog(this, "记录已更新!");
        }
    }

    private void updateFile() {
        // 更新文件中的数据
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String type = (String) tableModel.getValueAt(i, 0);
                String amount = (String) tableModel.getValueAt(i, 1);
                String description = (String) tableModel.getValueAt(i, 2);
                String category = (String) tableModel.getValueAt(i, 3);
                String time = (String) tableModel.getValueAt(i, 4);

                writer.write(type + "," + amount + "," + description + "," + category + "," + time);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRecords() {
        // 清空表格
        tableModel.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                tableModel.addRow(data); // 将记录添加到表格中
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    String record = data[0] + "," + data[1] + "," + data[2] + "," + data[3] + "," + data[4];
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                        writer.write(record);
                        writer.newLine();
                    }
                }
                JOptionPane.showMessageDialog(this, "CSV文件导入成功!");
                loadRecords(); // 更新显示的记录
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void classifyExpenses() {
        // 创建一个模态加载对话框
        JDialog loadingDialog = new JDialog(this, "正在处理...", true);
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.add(new JLabel("正在获取分类结果，请稍候..."), BorderLayout.CENTER);
        loadingDialog.getContentPane().add(loadingPanel);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);

        // 使用 SwingWorker 在后台调用 DeepSeek
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // 构造请求字符串
                StringBuilder inputBuilder = new StringBuilder();
                inputBuilder.append("请对以下个人理财记录数据根据用途进行智能分类，并补充分类信息。直接将数据进行输出，不需要多余的解释，以文本的形式进行输出即可，不用使用markdown形式\n");
                inputBuilder.append("请保持原始数据格式，格式为：类型,金额,用途,分类,时间\n");
                inputBuilder.append("下面是数据：\n");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String type = (String) tableModel.getValueAt(i, 0);
                    String amount = (String) tableModel.getValueAt(i, 1);
                    String usage = (String) tableModel.getValueAt(i, 2);
                    String category = (String) tableModel.getValueAt(i, 3); // 可能为空
                    String time = (String) tableModel.getValueAt(i, 4);
                    inputBuilder.append(type).append(",")
                            .append(amount).append(",")
                            .append(usage).append(",")
                            .append(category).append(",")
                            .append(time).append("\n");
                }
                String prompt = inputBuilder.toString();
                return DeepSeekUtil.getResponse(prompt);
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // 关闭加载对话框
                try {
                    String response = get();
                    System.out.println(response);
                    String[] lines = response.split("\\r?\\n");
                    if (lines.length < tableModel.getRowCount()) {
                        JOptionPane.showMessageDialog(MainFrame.this, "分类结果数量与记录数量不符，请检查DeepSeek返回的结果！", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // 更新表格数据
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String line = lines[i].trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split(",");
                        if (parts.length != 5) continue;
                        tableModel.setValueAt(parts[0].trim(), i, 0);
                        tableModel.setValueAt(parts[1].trim(), i, 1);
                        tableModel.setValueAt(parts[2].trim(), i, 2);
                        tableModel.setValueAt(parts[3].trim(), i, 3);
                        tableModel.setValueAt(parts[4].trim(), i, 4);
                    }
                    // 更新文件保存的数据
                    updateFile();
                    JOptionPane.showMessageDialog(MainFrame.this, "费用分类已完成!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "发生错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }





    private void showInsights() {
        // 创建加载对话框
        JDialog loadingDialog = new JDialog(this, "正在处理...", true);
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.add(new JLabel("正在获取消费洞察，请稍候..."), BorderLayout.CENTER);
        loadingDialog.getContentPane().add(loadingPanel);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // 构造请求字符串
                StringBuilder promptBuilder = new StringBuilder();
                promptBuilder.append("请基于以下个人消费记录数据进行消费洞察与预测。\n");
                promptBuilder.append("数据格式为：类型,金额,用途,分类,时间。\n");
                promptBuilder.append("要求：\n");
                promptBuilder.append("1. 根据用户的消费行为，给出每月预算建议、储蓄目标以及削减成本的建议；\n");
                promptBuilder.append("2. 考虑中国本地财务背景，检测季节性消费习惯（例如春节期间支出较高），并针对这些情况提出具体建议。\n");
                promptBuilder.append("下面是数据：\n");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String type = (String) tableModel.getValueAt(i, 0);
                    String amount = (String) tableModel.getValueAt(i, 1);
                    String usage = (String) tableModel.getValueAt(i, 2);
                    String category = (String) tableModel.getValueAt(i, 3);
                    String time = (String) tableModel.getValueAt(i, 4);
                    promptBuilder.append(type).append(",")
                            .append(amount).append(",")
                            .append(usage).append(",")
                            .append(category).append(",")
                            .append(time).append("\n");
                }
                String prompt = promptBuilder.toString();
                return DeepSeekUtil.getResponse(prompt);
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // 关闭加载对话框
                try {
                    String response = get();
                    // 使用 CommonMark 解析 Markdown
                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(response);
                    HtmlRenderer renderer = HtmlRenderer.builder().build();
                    String htmlContent = renderer.render(document);

                    // 构造 HTML 视图
                    JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
                    editorPane.setEditable(false);
                    editorPane.setPreferredSize(new Dimension(600, 400));
                    JScrollPane scrollPane = new JScrollPane(editorPane);
                    scrollPane.setPreferredSize(new Dimension(600, 400));

                    JOptionPane.showMessageDialog(MainFrame.this, scrollPane, "消费洞察与预测", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "发生错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }



    private void resetForm() {
        // 清空表单字段
        amountField.setText("");
        descriptionField.setText("");
        categoryField.setText("");
        typeComboBox.setSelectedIndex(0);

        // 如果之前选中了某一行，取消选中
        recordsTable.clearSelection();

        // 重置选中的行索引
        selectedRow = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

"// tiny extra line for ahead test" 
