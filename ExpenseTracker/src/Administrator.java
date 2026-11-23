public class Administrator extends User {
    private static int counter = 1;
    private final int adminId;

    public Administrator(String name, String email, String password) {
        super(name, email, password);
        this.adminId = counter++;
    }
    public int getAdminId() { return adminId; }
    @Override public String getRole() { return "Administrator"; }

    // Remove a user account. Returns false if it is this admin.
    public boolean manageUsers(User target) {
        if (target == this) return false;
        return DataStore.users.remove(target);
    }

    // target == null -> add newName | newName == null -> delete target | otherwise rename target
    public boolean manageCategories(Category target, String newName) {
        if (target == null) {
            if (newName == null || newName.isBlank() || exists(newName)) return false;
            DataStore.categories.add(new Category(newName.trim(), false));
            return true;
        }
        if (newName == null) {
            if (target.isDefault()) return false;
            return DataStore.categories.remove(target);
        }
        if (newName.isBlank() || exists(newName)) return false;
        target.updateCategory(newName.trim());
        return true;
    }

    private boolean exists(String name) {
        for (Category c : DataStore.categories) {
            if (c.getName().equalsIgnoreCase(name.trim())) return true;
        }
        return false;
    }
}