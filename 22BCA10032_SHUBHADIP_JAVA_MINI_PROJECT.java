import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ToDoListApp extends Frame {
    private TextField taskInput;
    private Choice priorityChoice;
    private Panel taskListPanel, highTasks, mediumTasks, lowTasks;
    private ArrayList<Task> tasks;
    private ArrayList<Checkbox> checkboxes;
    private Button addButton, editButton, deleteButton, completeButton;

    public ToDoListApp() {
        setTitle("  To-Do List App");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F4F4F4"));

        tasks = new ArrayList<>();
        checkboxes = new ArrayList<>();

        // Input Panel
        Panel inputPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        taskInput = new TextField(25);
        priorityChoice = new Choice();
        priorityChoice.add("High");
        priorityChoice.add("Medium");
        priorityChoice.add("Low");

        addButton = createStyledButton("Add Task");
        editButton = createStyledButton(" Edit");
        deleteButton = createStyledButton("Delete");
        completeButton = createStyledButton(" Complete");

        addButton.addActionListener(e -> addTask());
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTasks());
        completeButton.addActionListener(e -> completeSelectedTasks());

        inputPanel.add(new Label("Task:"));
        inputPanel.add(taskInput);
        inputPanel.add(new Label("Priority:"));
        inputPanel.add(priorityChoice);
        inputPanel.add(addButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);
        inputPanel.add(completeButton);

        // Column layout panel
        taskListPanel = new Panel(new GridLayout(1, 3, 10, 10));
        taskListPanel.setBackground(Color.decode("#EAEAEA"));

        highTasks = createPriorityColumn(" High Priority", "#FFCDD2");
        mediumTasks = createPriorityColumn("Medium Priority", "#FFF9C4");
        lowTasks = createPriorityColumn(" Low Priority", "#C8E6C9");

        taskListPanel.add(highTasks.getParent());
        taskListPanel.add(mediumTasks.getParent());
        taskListPanel.add(lowTasks.getParent());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(taskListPanel);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        setVisible(true);
        setResizable(true);
        setLocationRelativeTo(null);
    }

    private Panel createPriorityColumn(String title, String bgColorHex) {
        Panel columnWrapper = new Panel(new BorderLayout());
        columnWrapper.setBackground(Color.decode(bgColorHex));

        Label header = new Label(title, Label.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.decode(bgColorHex));
        columnWrapper.add(header, BorderLayout.NORTH);

        Panel taskList = new Panel();
        taskList.setLayout(new BoxLayout(taskList, BoxLayout.Y_AXIS));
        taskList.setBackground(Color.decode(bgColorHex));
        columnWrapper.add(taskList, BorderLayout.CENTER);

        switch (title) {
            case " High Priority" -> highTasks = taskList;
            case " Medium Priority" -> mediumTasks = taskList;
            case " Low Priority" -> lowTasks = taskList;
        }

        return taskList;
    }

    private void addTask() {
        String description = taskInput.getText().trim();
        int priority = priorityChoice.getSelectedIndex() + 1;

        if (!description.isEmpty()) {
            Task newTask = new Task(description, priority);
            tasks.add(newTask);
            taskInput.setText("");
            updateTaskList();
        }
    }

    private void editSelectedTask() {
        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).getState()) {
                Task task = tasks.get(i);
                taskInput.setText(task.getDescription());
                priorityChoice.select(task.getPriority() - 1);
                tasks.remove(i);
                updateTaskList();
                break;
            }
        }
    }

    private void deleteSelectedTasks() {
        for (int i = checkboxes.size() - 1; i >= 0; i--) {
            if (checkboxes.get(i).getState()) {
                tasks.remove(i);
            }
        }
        updateTaskList();
    }

    private void completeSelectedTasks() {
        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).getState()) {
                tasks.get(i).setCompleted(true);
            }
        }
        updateTaskList();
    }

    private void updateTaskList() {
        Collections.sort(tasks, Comparator.comparingInt(Task::getPriority));
        highTasks.removeAll();
        mediumTasks.removeAll();
        lowTasks.removeAll();
        checkboxes.clear();

        for (Task task : tasks) {
            String text = task.toString();
            Checkbox checkbox = new Checkbox(text);
            checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            checkboxes.add(checkbox);

            switch (task.getPriority()) {
                case 1 -> highTasks.add(checkbox);
                case 2 -> mediumTasks.add(checkbox);
                case 3 -> lowTasks.add(checkbox);
            }
        }

        highTasks.revalidate();
        mediumTasks.revalidate();
        lowTasks.revalidate();
        highTasks.repaint();
        mediumTasks.repaint();
        lowTasks.repaint();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setBackground(Color.decode("#1976D2"));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
    }

    public static void main(String[] args) {
        new ToDoListApp();
    }
}

class Task {
    private String description;
    private int priority;
    private boolean completed;

    public Task(String description, int priority) {
        this.description = description;
        this.priority = priority;
        this.completed = false;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        String priorityString = switch (priority) {
            case 1 -> "High";
            case 2 -> "Medium";
            case 3 -> "Low";
            default -> "Unknown";
        };
        String status = completed ? " Done" : "";
        return description + " (Priority: " + priorityString + ")" + status;
    }
}