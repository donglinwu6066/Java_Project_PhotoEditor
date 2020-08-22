import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class LayersData {
    //private final SimpleStringProperty number;
    private final StringProperty fileName;
    private CheckBox checkbox;  
    //String number,
    LayersData(String fName) {
        this.fileName = new SimpleStringProperty(fName);
        this.checkbox = new CheckBox();     
        this.checkbox.setSelected(true); 
    }
    public final StringProperty fileNameProperty() {
        return this.fileName;
    }     
    public String getFileName() {
        return fileName.get();
    }
    public void setFileName(String fName) {
        fileName.set(fName);
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }
 
    public void setCheckBox(CheckBox checkbox) {
        this.checkbox = checkbox;
    }
    public boolean isVisible() {
        return checkbox.isSelected();
    }
    
    
    
}
