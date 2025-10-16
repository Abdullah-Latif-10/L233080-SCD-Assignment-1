package assingment2;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
// removed unused java.nio.file import
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ProjectAppGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    // Project model
    private List<Task> tasks = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();

    // UI components
    private JTable taskTable;
    private TaskTableModel taskTableModel = new TaskTableModel();

    private JTextField tasksField = new JTextField(24);
    private JTextField resourcesField = new JTextField(24);

    private JButton uploadTasksBtn = new JButton("Upload Tasks");
    private JButton uploadResourcesBtn = new JButton("Upload Resources");
    private JButton newProjBtn = new JButton("New");
    private JButton saveProjBtn = new JButton("Save");
    private JButton closeProjBtn = new JButton("Close");
    private JButton analyzeBtn = new JButton("Analyze");

    // Analyze options
    private JRadioButton optCompletion = new JRadioButton("Project completion & duration", true);
    private JRadioButton optOverlap = new JRadioButton("Overlapping tasks");
    private JRadioButton optTeam = new JRadioButton("Resources and teams");
    private JRadioButton optEffort = new JRadioButton("Effort breakdown (resource-wise)");
    private ButtonGroup analyzeGroup = new ButtonGroup();

    private JTextArea analysisOutput = new JTextArea(12, 80);

    // Visualizer
    private GanttPanel ganttPanel = new GanttPanel();

    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyyMMdd+HHmm");

    public ProjectAppGUI() {
        super("Project Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Project", createProjectPanel());
        tabs.addTab("Analyze", createAnalyzePanel());
        tabs.addTab("Visualize", createVisualizePanel());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private JPanel createProjectPanel() {
        JPanel p = new JPanel(new BorderLayout());

        // Top toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(newProjBtn);
        toolbar.add(saveProjBtn);
        toolbar.add(closeProjBtn);
        toolbar.add(new JLabel("Project:"));
        toolbar.add(Box.createHorizontalStrut(20));
        p.add(toolbar, BorderLayout.NORTH);

    // Center: single tasks table (includes resources column)
    taskTable = new JTable(taskTableModel);
    JScrollPane taskSP = new JScrollPane(taskTable);
    taskSP.setBorder(BorderFactory.createTitledBorder("Tasks"));
    p.add(taskSP, BorderLayout.CENTER);

    // Bottom: upload controls (no add forms)
    JPanel bottom = new JPanel(new BorderLayout());

    JPanel upload = new JPanel(new FlowLayout(FlowLayout.LEFT));
    upload.add(new JLabel("Tasks file:"));
    upload.add(tasksField);
    upload.add(uploadTasksBtn);
    upload.add(new JLabel("Resources file:"));
    upload.add(resourcesField);
    upload.add(uploadResourcesBtn);
    bottom.add(upload, BorderLayout.NORTH);

    // Instructions for manual additions
    JPanel instruct = new JPanel(new FlowLayout(FlowLayout.LEFT));
    instruct.add(new JLabel("Date format: yyyyMMdd+HHmm (e.g. 20251016+0930). Resource allocations: taskId:percent,... (e.g. 1:50,3:100)"));
    bottom.add(instruct, BorderLayout.SOUTH);

    // add forms back (per request)
    JPanel forms = new JPanel(new GridLayout(1,2));
    forms.add(createAddTaskPanel());
    forms.add(createAddResourcePanel());
    bottom.add(forms, BorderLayout.CENTER);

    p.add(bottom, BorderLayout.SOUTH);

        attachProjectHandlers();
        return p;
    }

        // removed add-task and add-resource panels per request

    private JPanel createAnalyzePanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel opts = new JPanel(new GridLayout(4,1));
        analyzeGroup.add(optCompletion); analyzeGroup.add(optOverlap); analyzeGroup.add(optTeam); analyzeGroup.add(optEffort);
        opts.add(optCompletion); opts.add(optOverlap); opts.add(optTeam); opts.add(optEffort);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(opts);
        top.add(analyzeBtn);
        analyzeBtn.addActionListener(this::onAnalyze);
        p.add(top, BorderLayout.NORTH);
        analysisOutput.setEditable(false);
        analysisOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        p.add(new JScrollPane(analysisOutput), BorderLayout.CENTER);
        return p;
    }

    private JPanel createAddTaskPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Add Task (format: yyyyMMdd+HHmm)"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridx=0; c.gridy=0; p.add(new JLabel("Id:"), c);
        JTextField idF = new JTextField(4); c.gridx=1; p.add(idF, c);
        c.gridx=0; c.gridy=1; p.add(new JLabel("Title:"), c);
        JTextField titleF = new JTextField(16); c.gridx=1; p.add(titleF, c);
        c.gridx=0; c.gridy=2; p.add(new JLabel("Start (yyyyMMdd+HHmm):"), c);
        JTextField startF = new JTextField(12); c.gridx=1; p.add(startF, c);
        c.gridx=0; c.gridy=3; p.add(new JLabel("End (yyyyMMdd+HHmm):"), c);
        JTextField endF = new JTextField(12); c.gridx=1; p.add(endF, c);
        c.gridx=0; c.gridy=4; p.add(new JLabel("Dependencies (comma sep ids):"), c);
        JTextField depsF = new JTextField(12); c.gridx=1; p.add(depsF, c);
        JButton add = new JButton("Add Task"); c.gridx=1; c.gridy=5; p.add(add, c);
        add.addActionListener(ev -> {
            try {
                int id = Integer.parseInt(idF.getText().trim());
                String title = titleF.getText().trim();
                String start = startF.getText().trim();
                String end = endF.getText().trim();
                List<Integer> deps = new ArrayList<>();
                if (!depsF.getText().trim().isEmpty()) {
                    for (String s: depsF.getText().split(",")) deps.add(Integer.parseInt(s.trim()));
                }
                AtomicTask t = new AtomicTask(id, title, start, end, deps);
                tasks.add(t);
                taskTableModel.fireTableDataChanged();
                ganttPanel.setTasks(tasks);
            } catch (Exception ex) { showException(ex); }
        });
        return p;
    }

    private JPanel createAddResourcePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Add Resource (allocs: taskId:percent,...)"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridx=0; c.gridy=0; p.add(new JLabel("Name:"), c);
        JTextField nameF = new JTextField(12); c.gridx=1; p.add(nameF, c);
        c.gridx=0; c.gridy=1; p.add(new JLabel("Allocations:"), c);
        JTextField allocF = new JTextField(16); c.gridx=1; p.add(allocF, c);
        JButton add = new JButton("Add Resource"); c.gridx=1; c.gridy=2; p.add(add, c);
        add.addActionListener(ev -> {
            try {
                String name = nameF.getText().trim();
                if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter resource name"); return; }
                Resource r = new Resource(name);
                if (!allocF.getText().trim().isEmpty()) {
                    for (String part: allocF.getText().trim().split(",")) {
                        String token = part.trim();
                        if (token.isEmpty()) continue;
                        String[] kv = token.split(":");
                        if (kv.length != 2) continue;
                        int taskId = Integer.parseInt(kv[0].trim());
                        int percent = Integer.parseInt(kv[1].trim());
                        // find task by id
                        Task task = null;
                        for (Task tt : tasks) { if (tt.getId() == taskId) { task = tt; break; } }
                        if (task != null) {
                            Allocation a = new Allocation(r, task, percent);
                            r.addAllocation(a);
                            r.addTaskEffort(taskId, percent);
                        }
                    }
                }
                resources.add(r);
                taskTableModel.fireTableDataChanged();
                ganttPanel.setTasks(tasks);
                JOptionPane.showMessageDialog(this, "Resource added: " + r.getName());
            } catch (Exception ex) { showException(ex); }
        });
        return p;
    }

    private JPanel createVisualizePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(ganttPanel, BorderLayout.CENTER);
        JPanel ctrls = new JPanel(new FlowLayout(FlowLayout.LEFT));
    p.add(ctrls, BorderLayout.NORTH);
        return p;
    }

    private void attachProjectHandlers() {
        uploadTasksBtn.addActionListener(this::onUploadTasks);
        
        uploadResourcesBtn.addActionListener(this::onUploadResources);

        newProjBtn.addActionListener(this::onNewProject);
        closeProjBtn.addActionListener(this::onClose);
        saveProjBtn.addActionListener(this::onSave);
    }


    private void onUploadTasks(ActionEvent ignored) {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            tasksField.setText(fc.getSelectedFile().getAbsolutePath());
            try {
                List<Task> loaded = ProjectLoader.loadTasks(tasksField.getText());
                tasks.clear(); tasks.addAll(loaded);
                taskTableModel.fireTableDataChanged();
                ganttPanel.setTasks(tasks);
                JOptionPane.showMessageDialog(this, "Tasks loaded: "+tasks.size());
            } catch (IOException | NumberFormatException ex) { 
                showException(ex); 
            }
        }
    }

    private void onUploadResources(ActionEvent ignored) {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            resourcesField.setText(fc.getSelectedFile().getAbsolutePath());
            try {
                List<Resource> loaded = ProjectLoader.loadResources(resourcesField.getText(), tasks);
                resources.clear(); resources.addAll(loaded);
                taskTableModel.fireTableDataChanged();
                JOptionPane.showMessageDialog(this, "Resources loaded: "+resources.size());
            } catch (IOException | NumberFormatException ex) { 
                showException(ex); 
            }
        }
    }

    private void onNewProject(ActionEvent ignored) {
        tasks.clear(); resources.clear(); taskTableModel.fireTableDataChanged(); ganttPanel.setTasks(tasks);
    }

    private void onClose(ActionEvent ignored) { 
        dispose(); 
    }

    private void onSave(ActionEvent ignored) {
        try {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            fc.setDialogTitle("Save tasks file");
            if (fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
                File tf = fc.getSelectedFile();
                try (PrintWriter pw = new PrintWriter(tf)) {
                    // write tasks in same format as loader
                    pw.println("# TASKS");
                    for (Task t : tasks) {
                        String deps = "";
                        List<Integer> d = t.getDependencies();
                        if (d!=null && !d.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (int i=0;i<d.size();i++) { if (i>0) sb.append(','); sb.append(d.get(i)); }
                            deps = sb.toString();
                        }
                        // attempt to extract start/end from AtomicTask
                        String start = ""; String end = "";
                        if (t instanceof AtomicTask at) {
                            start = at.getStart().format(INPUT_FMT);
                            end = at.getEnd().format(INPUT_FMT);
                        }
                        pw.println(t.getId()+","+t.getTitle()+","+start+","+end+ (deps.isEmpty()?"":" ,"+deps));
                    }

                    pw.println(); pw.println("# RESOURCES");
                    for (Resource r : resources) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(r.getName());
                        // get allocations from taskEfforts map
                        for (var entry : r.getTaskEfforts().entrySet()) {
                            sb.append(",").append(entry.getKey()).append(":").append(entry.getValue());
                        }
                        pw.println(sb.toString());
                    }
                }
                JOptionPane.showMessageDialog(this, "Tasks saved to " + tf.getAbsolutePath());
            }
        } catch (Exception ex) { showException(ex); }
    }

    private void onAnalyze(ActionEvent e) {
        runSelectedAnalysis();
        // e.equals(e);
    }

    private void runSelectedAnalysis() {
        if (tasks.isEmpty() || resources.isEmpty()) { JOptionPane.showMessageDialog(this, "Please load or add tasks and resources first.", "No data", JOptionPane.WARNING_MESSAGE); return; }
        Project project = new Project();
        for (Task t: tasks) project.addTask(t);
        for (Resource r: resources) project.addResource(r);

        String out = capturePrints(() -> {
            if (optCompletion.isSelected()) project.printCompletionAndDuration();
            else if (optOverlap.isSelected()) project.printOverlappingTasks();
            else if (optTeam.isSelected()) {
                // ask user for task id
                String s = JOptionPane.showInputDialog(this, "Enter task id for team lookup:", "Task Id", JOptionPane.QUESTION_MESSAGE);
                try {
                    int tid = Integer.parseInt((s==null||s.trim().isEmpty())?"0":s.trim());
                    project.printTeamForTask(tid);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid task id");
                }
            }
            else if (optEffort.isSelected()) project.printTotalEffortPerResource();
        });
        analysisOutput.setText(out);
    }

    // Helpers
    // Compose resources string for a given task id. Mark partial allocations (percent < 100) with '*'.
    private String resourcesForTask(int taskId) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Resource r : resources) {
            Integer pct = r.getTaskEfforts().get(taskId);
            if (pct != null) {
                if (!first) sb.append(", ");
                sb.append(r.getName());
                if (pct < 100) sb.append("*");
                first = false;
            }
        }
        return sb.toString();
    }

    private void showException(Exception ex) {
        StringWriter sw = new StringWriter(); ex.printStackTrace(new PrintWriter(sw));
        JOptionPane.showMessageDialog(this, sw.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Capture System.out prints of runnable and return collected string
    private String capturePrints(Runnable r) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        try {
            System.setOut(ps);
            r.run();
        } finally {
            System.out.flush(); System.setOut(oldOut);
        }
        return baos.toString();
    }

    // Table models
    private class TaskTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private final String[] cols = {"Id","Title","Start","End","Dependencies","Resources"};
        @Override public int getRowCount(){ return tasks.size(); }
        @Override public int getColumnCount(){ return cols.length; }
        @Override public String getColumnName(int c){ return cols[c]; }
        @Override
        public Object getValueAt(int r,int c){
            Task t = tasks.get(r);
            return switch(c){
                case 0 -> t.getId();
                case 1 -> t.getTitle();
                case 2 -> (t.getStart()!=null) ? ((t instanceof AtomicTask at)? at.getStart().format(INPUT_FMT) : t.getStart().toString()) : "";
                case 3 -> (t.getEnd()!=null) ? ((t instanceof AtomicTask at)? at.getEnd().format(INPUT_FMT) : t.getEnd().toString()) : "";
                case 4 -> t.getDependencies();
                case 5 -> resourcesForTask(t.getId());
                default -> null;
            };
        }
    }

    // Simple Gantt renderer
    private static class GanttPanel extends JPanel {
        private static final long serialVersionUID = 1L;
    private List<Task> tasks = new ArrayList<>();
    private LocalDateTime min = null;
    // adjustable layout
    private final int leftMargin = 60;
    private final  int topMargin = 32;
    private final  int barHeight = 12;
    private final  int vSpacing = 32; // doubled spacing per request (looser)
    // private int hourWidth = 8; // pixels per hour (looser)

 
        public GanttPanel(){ setPreferredSize(new Dimension(800, 300)); }
        public void setTasks(List<Task> tasks){ this.tasks = tasks; computeMin(); repaint(); }
        private void computeMin(){ min = null; for (Task t: tasks){ if (t.getStart()!=null){ if (min==null || t.getStart().isBefore(min)) min = t.getStart(); } } }
        
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if (tasks==null || tasks.isEmpty() || min==null) return;
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Layout parameters
            int labelWidth = this.leftMargin;
            int topPadding = this.topMargin;
            int rowHeight = Math.max(this.barHeight + this.vSpacing, 20);
            int colCount = Math.max(1, tasks.size());
            int availableW = Math.max(200, getWidth() - labelWidth - 20);
            int colWidth = availableW / colCount;
            int barWidth = Math.max(10, colWidth - 20);

            // Find minMonth and maxMonth
            LocalDateTime minMonth = min.withDayOfMonth(1);
            LocalDateTime max = min;
            for (Task t: tasks) if (t.getEnd()!=null && t.getEnd().isAfter(max)) max = t.getEnd();
            LocalDateTime maxMonth = max.withDayOfMonth(1).plusMonths(1);
            long months = java.time.temporal.ChronoUnit.MONTHS.between(minMonth, maxMonth);

            // Draw background
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw horizontal month grid and labels (y-axis)
            DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MM-yy");
            for (int i=0;i<months;i++){
                int y = topPadding + i*rowHeight;
                // alternate row color for clarity
                if (i%2==0) g2.setColor(new Color(240,240,255));
                else g2.setColor(new Color(225,225,240));
                g2.fillRect(0, y, getWidth(), rowHeight);
                // grid line
                g2.setColor(new Color(200,200,200));
                g2.drawLine(labelWidth, y, getWidth(), y);
                // month label (date)
                g2.setColor(Color.BLACK);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
                LocalDateTime lbl = minMonth.plusMonths(i);
                g2.drawString(lbl.format(monthFmt), 8, y + rowHeight/2 + 5);
            }

            // Draw task columns and vertical separators, labels at top
            for (int ti=0; ti<tasks.size(); ti++){
                // column separator
                g2.setColor(new Color(200,200,200));
                g2.drawLine(labelWidth + ti*colWidth, topPadding, labelWidth + ti*colWidth, topPadding + (int)months*rowHeight);
                // task label
                g2.setColor(Color.BLACK);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
                String tlabel = "T"+tasks.get(ti).getId();
                FontMetrics fm = g2.getFontMetrics();
                int tx = labelWidth + ti*colWidth + (colWidth - fm.stringWidth(tlabel))/2;
                g2.drawString(tlabel, tx, topPadding - 6);
            }

            // Draw vertical bars per task covering start->end months
            for (int ti=0; ti<tasks.size(); ti++){
                Task t = tasks.get(ti);
                if (t.getStart()==null || t.getEnd()==null) continue;
                long startIdx = java.time.temporal.ChronoUnit.MONTHS.between(minMonth, t.getStart().withDayOfMonth(1));
                long endIdx = java.time.temporal.ChronoUnit.MONTHS.between(minMonth, t.getEnd().withDayOfMonth(1));
                int y1 = topPadding + (int)startIdx * rowHeight + 4;
                int y2 = topPadding + (int)endIdx * rowHeight + rowHeight - 6;
                int bx = labelWidth + ti*colWidth + (colWidth - barWidth)/2;
                int bh = Math.max(6, y2 - y1);

                GradientPaint gp = new GradientPaint(bx, y1, new Color(80,140,220), bx+barWidth, y1+bh, new Color(30,90,180));
                g2.setPaint(gp);
                g2.fillRect(bx, y1, barWidth, bh);
                g2.setColor(new Color(30,90,180));
                g2.drawRect(bx, y1, barWidth, bh);

                // small labels for start/end
                g2.setColor(Color.BLACK);
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 10f));
                g2.drawString(t.getStart().format(DateTimeFormatter.ofPattern("dd-MM-yy")), bx, y1+13);
                g2.drawString(t.getEnd().format(DateTimeFormatter.ofPattern("dd-MM-yy")), bx, y2-2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProjectAppGUI g = new ProjectAppGUI();
            g.setVisible(true);
        });
    }
}
