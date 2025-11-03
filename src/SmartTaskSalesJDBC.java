import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class JDBCUtils {
    static final String DB_URL = "jdbc:mysql://localhost:3306/salesdb?serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "Root";

    public static Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException(e); }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}

class User {
    private final String id, name, password, role;
    public User(String id, String name, String password, String role) { this.id = id; this.name = name; this.password = password; this.role = role; }
    public String id() { return id; } public String name() { return name; }
    public String password() { return password; } public String role() { return role; }
}

class Task {
    private final String id, title, assignedToUserId, status;
    private final boolean done;
    private final LocalDate due;
    public Task(String id, String title, boolean done, LocalDate due, String assignedToUserId, String status) {
        this.id = id; this.title = title; this.done = done; this.due = due; this.assignedToUserId = assignedToUserId; this.status = status;
    }
    public String id() { return id; } public String title() { return title; }
    public boolean done() { return done; } public LocalDate due() { return due; }
    public String assignedToUserId() { return assignedToUserId; } public String status() { return status; }
}

class Sale {
    private final String id, taskId;
    private final double amount;
    private final LocalDate date;
    public Sale(String id, String taskId, double amount, LocalDate date) {
        this.id = id; this.taskId = taskId; this.amount = amount; this.date = date;
    }
    public String id() { return id; } public String taskId() { return taskId; }
    public double amount() { return amount; } public LocalDate date() { return date; }
}

class UserDAO {
    public java.util.List<User> getAll() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = JDBCUtils.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(new User(rs.getString("id"), rs.getString("name"), rs.getString("password"), rs.getString("role")));
        } catch (SQLException ex) { throw new RuntimeException(ex); }
        return list;
    }
    public User getByNameAndPass(String name, String pass) {
        String sql = "SELECT * FROM users WHERE name=? AND password=?";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new User(rs.getString("id"), rs.getString("name"), rs.getString("password"), rs.getString("role"));
        } catch(SQLException ex){ throw new RuntimeException(ex); }
        return null;
    }
    public void addUser(User u) {
        String sql = "INSERT INTO users (id, name, password, role) VALUES(?,?,?,?)";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.id()); ps.setString(2, u.name()); ps.setString(3, u.password()); ps.setString(4, u.role());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public void updateUser(User u) {
        String sql = "UPDATE users SET name=?, password=?, role=? WHERE id=?";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.name()); ps.setString(2, u.password()); ps.setString(3, u.role()); ps.setString(4, u.id());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public void deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public String nextUserId() {
        String sql = "SELECT id FROM users WHERE id LIKE 'USR%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = JDBCUtils.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int num = Integer.parseInt(lastId.substring(3));
                return String.format("USR%03d", num + 1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return "USR001";
    }
}

class TaskDAO {
    public java.util.List<Task> getAll() {
        java.util.List<Task> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = JDBCUtils.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate d = rs.getDate("due") != null ? rs.getDate("due").toLocalDate() : null;
                list.add(new Task(rs.getString("id"), rs.getString("title"), rs.getBoolean("done"), d, rs.getString("assignedToUserId"), rs.getString("status")));
            }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
        return list;
    }
    public void addTask(Task t) {
        String sql = "INSERT INTO tasks (id, title, done, due, assignedToUserId, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.id()); ps.setString(2, t.title()); ps.setBoolean(3, t.done());
            if (t.due() != null) ps.setDate(4, java.sql.Date.valueOf(t.due())); else ps.setNull(4, Types.DATE);
            ps.setString(5, t.assignedToUserId()); ps.setString(6, t.status()); ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public void updateTask(Task t) {
        String sql = "UPDATE tasks SET title=?, done=?, due=?, assignedToUserId=?, status=? WHERE id=?";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.title()); ps.setBoolean(2, t.done());
            if (t.due() != null) ps.setDate(3, java.sql.Date.valueOf(t.due())); else ps.setNull(3, Types.DATE);
            ps.setString(4, t.assignedToUserId()); ps.setString(5, t.status()); ps.setString(6, t.id());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public void deleteTask(String id) {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public String nextTaskId() {
        String sql = "SELECT id FROM tasks WHERE id LIKE 'TSK%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = JDBCUtils.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int num = Integer.parseInt(lastId.substring(3));
                return String.format("TSK%03d", num + 1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return "TSK001";
    }
}

class SaleDAO {
    public java.util.List<Sale> getAll() {
        java.util.List<Sale> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM sales";
        try (Connection conn = JDBCUtils.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate d = rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null;
                list.add(new Sale(rs.getString("id"), rs.getString("taskId"), rs.getDouble("amount"), d));
            }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
        return list;
    }
    public void addSale(Sale s) {
        String sql = "INSERT INTO sales (id, taskId, amount, date) VALUES (?,?,?,?)";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.id()); ps.setString(2, s.taskId()); ps.setDouble(3, s.amount());
            if (s.date() != null) ps.setDate(4, java.sql.Date.valueOf(s.date())); else ps.setNull(4, Types.DATE);
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public void updateSale(Sale s) {
        String sql = "UPDATE sales SET taskId=?, amount=?, date=? WHERE id=?";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.taskId()); ps.setDouble(2, s.amount());
            if (s.date() != null) ps.setDate(3, java.sql.Date.valueOf(s.date())); else ps.setNull(3, Types.DATE);
            ps.setString(4, s.id()); ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public void deleteSale(String id) {
        String sql = "DELETE FROM sales WHERE id=?";
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
    public String nextSaleId() {
        String sql = "SELECT id FROM sales WHERE id LIKE 'SAL%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = JDBCUtils.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int num = Integer.parseInt(lastId.substring(3));
                return String.format("SAL%03d", num + 1);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return "SAL001";
    }
}

public class SmartTaskSalesJDBC extends JFrame {
    private final UserDAO userDao = new UserDAO();
    private final TaskDAO taskDao = new TaskDAO();
    private final SaleDAO saleDao = new SaleDAO();
    private User currentUser;

    private final DefaultTableModel userModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Role"}, 0);
    private final JTable userTable = new JTable(userModel);
    private final DefaultTableModel taskModel = new DefaultTableModel(
            new Object[]{"ID", "Title", "Done", "Due", "AssignedTo", "Status"}, 0);
    private final JTable taskTable = new JTable(taskModel);
    private final DefaultTableModel saleModel = new DefaultTableModel(
            new Object[]{"SaleID", "TaskID", "Amount", "Date"}, 0);
    private final JTable saleTable = new JTable(saleModel);

    private final JLabel totalSalesLbl = new JLabel("Total: 0.00");
    private final JTextArea byTaskArea = new JTextArea(6, 30);

    public SmartTaskSalesJDBC() {
        super("Smart Task Sales Tracker");
        login();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Users", buildUserPanel());
        tabs.add("Tasks", buildTaskPanel());
        tabs.add("Sales", buildSalesPanel());
        tabs.add("Summary", buildSummaryPanel());
        add(tabs);

        refreshAll();
    }
    private void login() {
        while (true) {
            java.util.List<User> allUsers = userDao.getAll();
            JComboBox<String> userBox = new JComboBox<>(allUsers.stream().map(User::name).toArray(String[]::new));
            JButton addUser = new JButton("Add User");
            JPanel p = new JPanel(new BorderLayout());
            p.add(new JLabel("Select user:"), BorderLayout.NORTH);
            p.add(userBox, BorderLayout.CENTER);
            p.add(addUser, BorderLayout.EAST);

            JPasswordField pw = new JPasswordField();
            JPanel loginPanel = new JPanel(new BorderLayout());
            loginPanel.add(p, BorderLayout.NORTH);
            loginPanel.add(new JLabel("Password:"), BorderLayout.CENTER);
            loginPanel.add(pw, BorderLayout.SOUTH);

            final boolean[] userAdded = {false};

            addUser.addActionListener(e -> {
                JTextField name = new JTextField();
                JPasswordField pass = new JPasswordField();
                JComboBox<String> roleBox = new JComboBox<>(new String[]{"Manager", "Employee"});
                int ok = JOptionPane.showConfirmDialog(p, new Object[]{"User name:", name, "Password:", pass, "Role:", roleBox}, "Add User", JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    String n = name.getText().trim(), pword = new String(pass.getPassword()), role = roleBox.getSelectedItem().toString();
                    String id = userDao.nextUserId();
                    userDao.addUser(new User(id, n, pword, role));
                    userBox.addItem(n); userBox.setSelectedItem(n); userAdded[0] = true;
                }
            });

            int ok = JOptionPane.showConfirmDialog(this, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (ok == JOptionPane.OK_OPTION && userBox.getSelectedItem() != null) {
                String enteredName = userBox.getSelectedItem().toString();
                String enteredPass = new String(pw.getPassword());
                currentUser = userDao.getByNameAndPass(enteredName, enteredPass);
                if (currentUser != null) break;
                else JOptionPane.showMessageDialog(this, "Invalid username or password. Try again.");
            } else if (!userAdded[0]) System.exit(0);
        }
    }
    private JPanel buildUserPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(new JScrollPane(userTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add User");
        JButton edit = new JButton("Edit User");
        JButton del = new JButton("Delete User");
        JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> onAddUser());
        edit.addActionListener(e -> onEditUser());
        del.addActionListener(e -> onDeleteUser());
        refresh.addActionListener(e -> refreshUsers());
        actions.add(add); actions.add(edit); actions.add(del); actions.add(refresh);
        root.add(actions, BorderLayout.NORTH);
        return root;
    }
    private void onAddUser() {
        JTextField name = new JTextField();
        JPasswordField pass = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Manager", "Employee"});
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"User name:", name, "Password:", pass, "Role:", roleBox}, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        String n = name.getText().trim();
        String p = new String(pass.getPassword());
        String r = roleBox.getSelectedItem().toString();
        String id = userDao.nextUserId();
        userDao.addUser(new User(id, n, p, r));
        refreshUsers();
    }
    private void onEditUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user row first."); return; }
        String id = userModel.getValueAt(row, 0).toString();
        java.util.List<User> users = userDao.getAll();
        for (User u : users) {
            if (u.id().equals(id)) {
                JTextField name = new JTextField(u.name());
                JPasswordField pass = new JPasswordField(u.password());
                JComboBox<String> roleBox = new JComboBox<>(new String[]{"Manager", "Employee"});
                roleBox.setSelectedItem(u.role());
                int ok = JOptionPane.showConfirmDialog(this, new Object[]{"User name:", name, "Password:", pass, "Role:", roleBox}, "Edit User", JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    userDao.updateUser(new User(u.id(), name.getText().trim(), new String(pass.getPassword()), roleBox.getSelectedItem().toString()));
                    refreshUsers();
                }
                break;
            }
        }
    }
    private void onDeleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user row first."); return; }
        String id = userModel.getValueAt(row, 0).toString();
        userDao.deleteUser(id);
        refreshUsers();
    }
    private void refreshUsers() {
        userModel.setRowCount(0);
        for (User u : userDao.getAll()) userModel.addRow(new Object[]{u.id(), u.name(), u.role()});
    }
    private JPanel buildTaskPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add Task");
        JButton edit = new JButton("Edit Task");
        JButton done = new JButton("Mark Done");
        JButton del = new JButton("Delete Task");
        JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> onAddTask()); edit.addActionListener(e -> onEditTask());
        done.addActionListener(e -> onMarkDone()); del.addActionListener(e -> onDeleteTask());
        refresh.addActionListener(e -> refreshTasks());
        actions.add(add); actions.add(edit); actions.add(done); actions.add(del); actions.add(refresh);
        root.add(actions, BorderLayout.NORTH);
        return root;
    }
    private void onAddTask() {
        JTextField title = new JTextField();
        JTextField due = new JTextField("YYYY-MM-DD");
        JComboBox<String> userList = new JComboBox<>(userDao.getAll().stream().map(User::id).toArray(String[]::new));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Backlog", "Sprint", "Done"});
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Title:", title, "Due (YYYY-MM-DD):", due, "Assign to (User ID):", userList, "Status:", statusBox}, "Add Task", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        String t = title.getText().trim();
        String d = due.getText().trim();
        LocalDate dueDate = null;
        if (!d.isBlank() && !d.equalsIgnoreCase("YYYY-MM-DD")) try { dueDate = LocalDate.parse(d); } catch(Exception ex){}
        String assigned = userList.getSelectedItem().toString();
        String stat = statusBox.getSelectedItem().toString();
        String id = taskDao.nextTaskId();
        taskDao.addTask(new Task(id, t, false, dueDate, assigned, stat));
        refreshTasks();
    }
    private void onEditTask() {
        int row = taskTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a task row first."); return; }
        String id = taskModel.getValueAt(row, 0).toString();
        java.util.List<Task> tasks = taskDao.getAll();
        for (Task t : tasks) {
            if (t.id().equals(id)) {
                JTextField title = new JTextField(t.title());
                JTextField due = new JTextField(t.due() != null ? t.due().toString() : "");
                JComboBox<String> userBox = new JComboBox<>(userDao.getAll().stream().map(User::id).toArray(String[]::new));
                userBox.setSelectedItem(t.assignedToUserId());
                JComboBox<String> statusBox = new JComboBox<>(new String[]{"Backlog", "Sprint", "Done"});
                statusBox.setSelectedItem(t.status());
                int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Title:", title, "Due (YYYY-MM-DD):", due, "Assign to (User ID):", userBox, "Status:", statusBox}, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    String newTitle = title.getText().trim();
                    LocalDate dueDate = null;
                    try { if (!due.getText().trim().isEmpty()) dueDate = LocalDate.parse(due.getText().trim()); } catch(Exception ex){}
                    String assigned = userBox.getSelectedItem().toString();
                    String stat = statusBox.getSelectedItem().toString();
                    taskDao.updateTask(new Task(t.id(), newTitle, t.done(), dueDate, assigned, stat));
                    refreshTasks();
                }
                break;
            }
        }
    }
    private void onMarkDone() {
        int row = taskTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a task row first."); return; }
        String id = taskModel.getValueAt(row, 0).toString();
        java.util.List<Task> tasks = taskDao.getAll();
        for (Task t : tasks) {
            if (t.id().equals(id) && !t.done()) {
                taskDao.updateTask(new Task(t.id(), t.title(), true, t.due(), t.assignedToUserId(), "Done"));
                refreshTasks();
                break;
            }
        }
    }
    private void onDeleteTask() {
        int row = taskTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a task row first."); return; }
        String id = taskModel.getValueAt(row, 0).toString();
        taskDao.deleteTask(id);
        refreshTasks();
    }
    private void refreshTasks() {
        taskModel.setRowCount(0);
        java.util.Map<String, String> userIdToName = new java.util.HashMap<>();
        for (User u : userDao.getAll()) userIdToName.put(u.id(), u.name());
        for (Task t : taskDao.getAll()) {
            String assignedName = userIdToName.getOrDefault(t.assignedToUserId(), "");
            taskModel.addRow(new Object[]{t.id(), t.title(), t.done(), t.due(), assignedName, t.status()});
        }
    }
    private JPanel buildSalesPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(new JScrollPane(saleTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add Sale");
        JButton edit = new JButton("Edit Sale");
        JButton del = new JButton("Delete Sale");
        JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> onAddSale());
        edit.addActionListener(e -> onEditSale());
        del.addActionListener(e -> onDeleteSale());
        refresh.addActionListener(e -> refreshSales());
        actions.add(add); actions.add(edit); actions.add(del); actions.add(refresh);
        root.add(actions, BorderLayout.NORTH);
        return root;
    }
    private void onAddSale() {
        JComboBox<String> taskIdBox = new JComboBox<>(taskDao.getAll().stream().map(Task::id).toArray(String[]::new));
        JTextField amount = new JTextField();
        JTextField date = new JTextField("YYYY-MM-DD");
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Task ID:", taskIdBox, "Amount:", amount, "Date (YYYY-MM-DD):", date}, "Add Sale", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        String tid = taskIdBox.getSelectedItem().toString();
        double amt;
        try { amt = Double.parseDouble(amount.getText().trim()); } catch (NumberFormatException ex) { return;}
        String d = date.getText().trim();
        LocalDate when = null;
        if (!d.isBlank() && !d.equalsIgnoreCase("YYYY-MM-DD")) try { when = LocalDate.parse(d); } catch(Exception ex){}
        String id = saleDao.nextSaleId();
        saleDao.addSale(new Sale(id, tid, amt, when));
        refreshSales();
    }
    private void onEditSale() {
        int row = saleTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a sale row first."); return; }
        String id = saleModel.getValueAt(row, 0).toString();
        java.util.List<Sale> sales = saleDao.getAll();
        for (Sale s : sales) {
            if (s.id().equals(id)) {
                JComboBox<String> taskBox = new JComboBox<>(taskDao.getAll().stream().map(Task::id).toArray(String[]::new));
                taskBox.setSelectedItem(s.taskId());
                JTextField amount = new JTextField(String.valueOf(s.amount()));
                JTextField date = new JTextField(s.date() != null ? s.date().toString() : "");
                int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Task ID:", taskBox, "Amount:", amount, "Date (YYYY-MM-DD):", date}, "Edit Sale", JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    String newTaskId = taskBox.getSelectedItem().toString();
                    double newAmt; try { newAmt = Double.parseDouble(amount.getText().trim()); } catch(Exception ex){ newAmt = s.amount(); }
                    LocalDate newDate; try { newDate = LocalDate.parse(date.getText().trim()); } catch(Exception ex){ newDate = s.date(); }
                    saleDao.updateSale(new Sale(s.id(), newTaskId, newAmt, newDate));
                    refreshSales();
                }
                break;
            }
        }
    }
    private void onDeleteSale() {
        int row = saleTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a sale row first."); return; }
        String id = saleModel.getValueAt(row, 0).toString();
        saleDao.deleteSale(id);
        refreshSales();
    }
    private void refreshSales() {
        saleModel.setRowCount(0);
        for (Sale s : saleDao.getAll()) saleModel.addRow(new Object[]{s.id(), s.taskId(), s.amount(), s.date()});
    }
    private JPanel buildSummaryPanel() {
        JPanel root = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton calc = new JButton("Recalculate");
        top.add(calc);
        calc.addActionListener(e -> refreshSummary());
        byTaskArea.setEditable(false);
        root.add(top, BorderLayout.NORTH);
        root.add(totalSalesLbl, BorderLayout.WEST);
        root.add(new JScrollPane(byTaskArea), BorderLayout.CENTER);
        return root;
    }
    private void refreshSummary() {
        double total = saleDao.getAll().stream().mapToDouble(Sale::amount).sum();
        totalSalesLbl.setText("Total: " + String.format("%.2f", total));
        java.util.Map<String, Double> byTask = new java.util.HashMap<>();
        for (Task t : taskDao.getAll()) byTask.put(t.id(), 0.0);
        for (Sale s : saleDao.getAll()) byTask.put(s.taskId(), byTask.getOrDefault(s.taskId(), 0.0) + s.amount());
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String,Double> e : byTask.entrySet())
            sb.append(e.getKey()).append(" = ").append(String.format("%.2f", e.getValue())).append("\n");
        byTaskArea.setText(sb.toString());
    }
    private void refreshAll() { refreshUsers(); refreshTasks(); refreshSales(); refreshSummary(); }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartTaskSalesJDBC().setVisible(true));
    }
}