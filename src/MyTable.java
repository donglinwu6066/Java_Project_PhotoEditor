import java.util.Collection;
import java.util.Collections;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;


public class MyTable extends TableView{
    public ObservableList<LayersData> layerData;
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    public MyTable(){
        TableColumn visibleCol = new TableColumn("Visible");
        TableColumn nameCol = new TableColumn("Name"); 
        super.setEditable(true);

        super.getColumns().addAll( visibleCol, nameCol);

        layerData = FXCollections.observableArrayList();

        visibleCol.setCellValueFactory(     
           new PropertyValueFactory<LayersData,String>("checkbox")
        );    
        
        Callback<TableColumn, TableCell> cellFactory =
            new Callback<TableColumn, TableCell>() {
                public TableCell call(TableColumn p) {
                   return new EditingCell();
                }
            };
        nameCol.setCellFactory(cellFactory);
        
        super.setRowFactory(tv -> {
            TableRow<LayersData> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    LayersData draggedPerson = (LayersData)super.getItems().remove(draggedIndex);

                    int dropIndex ; 

                    if (row.isEmpty()) {
                        dropIndex = super.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    super.getItems().add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    super.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });
            
            return row ;
        });
        
        nameCol.setCellValueFactory(
            new PropertyValueFactory<LayersData,String>("fileName")
        );
        nameCol.setOnEditCommit(
            new EventHandler<CellEditEvent<LayersData, String>>() {
                @Override
                public void handle(CellEditEvent<LayersData, String> t) {
                    ((LayersData) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setFileName(t.getNewValue());
                }
             }
        );


        super.setItems(layerData); 

        
    }
    public void swap(int a, int b){
        Collections.swap(layerData, a, b);
    }
    public void addlayerWindow(){
        Stage stage = new Stage();
 
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
    
        // How to center align content in a layout manager in JavaFX
        vbox.setAlignment(Pos.CENTER);
    
        Label label = new Label("Enter layer name");
        TextField fileName = new TextField();
        fileName.setPromptText("enter file name");
        Button enter = new Button("Enter");
        enter.setOnAction(e ->{
            addLayer(fileName.getText());
            stage.close();
        });
        
        vbox.getChildren().addAll(label, fileName, enter);
        Scene scene = new Scene(vbox, 250, 150);
        stage.setScene(scene);
        stage.show();
    }
    public void addLayer(String name){
        layerData.add(new LayersData(name));
        super.setItems(layerData); 

    }
    public void deletelayerWindow(int delete){
        deleteLayer(delete);
        // Stage stage = new Stage();
        // VBox vbox = new VBox();
        // vbox.setPadding(new Insets(10));
    
        // vbox.setAlignment(Pos.CENTER);
    
        // Label label = new Label("Enter delete layer number(top = 1)");
        // TextField fileName = new TextField();
        // fileName.setPromptText("enter name");
        // Button enter = new Button("Enter");
        // enter.setOnAction(  e ->{
            // deleteLayer(Integer.valueOf((fileName.getText().toString())));
            // stage.close();
        // });
    
        // vbox.getChildren().addAll(label, fileName, enter);
        // Scene scene = new Scene(vbox, 250, 150);
        // stage.setScene(scene);
        // stage.show();
    }
    private void deleteLayer(int index){
        if(index < 1)
            return;
        System.out.printf("enter %d ", index);
        layerData.remove(index);
        super.setItems(layerData); 

    }
    class EditingCell extends TableCell<LayersData, String> {
 
        private TextField textField;
 
        public EditingCell() {
        }
 
        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
 
            setText((String) getItem());
            setGraphic(null);
        }
 
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
 
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }
 
        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, 
                    Boolean arg1, Boolean arg2) {
                        if (!arg2) {
                            commitEdit(textField.getText());
                        }
                }
            });
        }
 
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

}