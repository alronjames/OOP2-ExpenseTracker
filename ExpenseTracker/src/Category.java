public class Category {
    private static int counter = 1;
    private final int categoryId;
    private String name;
    private final boolean isDefault;

    public Category(String name, boolean isDefault) {
        this.categoryId = counter++;
        this.name = name;
        this.isDefault = isDefault;
    }
    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public boolean isDefault() { return isDefault; }
    public void updateCategory(String newName) { this.name = newName; }
    @Override public String toString() { return name; }
}