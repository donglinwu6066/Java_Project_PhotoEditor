import java.io.File;
import java.lang.Integer;
import java.lang.Math;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
// import javafx.scene.*;
import javafx.scene.canvas.*;
// import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent; 
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color; 
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;

public class PhotoEditorController implements Initializable {
    // System is always "incorrect" 
    // with wrong syntax
    @FXML private BorderPane mainPane;
    // left panel
    @FXML private Circle sampleBrush;
    @FXML private GridPane centerGrid;
    // right panel
    @FXML private RadioButton noneRadio;
    @FXML private RadioButton negativeRadio;
    @FXML private RadioButton edgeDetectionRadio;
    @FXML private RadioButton grayScaleRadio;
    @FXML private RadioButton bluringRadio;
    @FXML private RadioButton brightnessRadio;
    @FXML private RadioButton medianRadio;
    @FXML private RadioButton mirrorRadio;
    @FXML private RadioButton sharpenRadio;
    @FXML private RadioButton embossRadio;
    @FXML private Label adjustScalingLabel;
    @FXML private Label adjustRotateLabel;
    @FXML private Label adjustUDLabel;
    @FXML private Label adjustLtRLabel;
    @FXML private Label adjustColorfulnessLabel;
    @FXML private Label adjustBlurLabel;
    @FXML private Label adjustAlphaLabel;
    @FXML private Label adjustBrightnessLabel;
    @FXML private Label brushSizeLabel;

    @FXML private Slider adjustScalingSlider;
    @FXML private Slider setPaintBrushSizeSlider;
    @FXML private Slider adjustRotateSlider;
    @FXML private Slider adjustUDSlider;
    @FXML private Slider adjustLtRSlider;
    @FXML private Slider adjustBrightnessSlider;
    @FXML private Slider adjustBlurSlider;
    @FXML private Slider adjustAlphaSlider;

    @FXML private TextField layerNumber;
    @FXML private TextField layerSwap;
    @FXML private TextField setPaintBrushSize;
    @FXML private TextField setTextSize;
    @FXML private TextField inputText;
    @FXML private Button createTextButton;
    @FXML private ColorPicker paintBrushColor;
    @FXML private ColorPicker paintFillColor;
    @FXML private ColorPicker fontColor;
    @FXML private Button clearPaintButton;
    @FXML private Button adjustResetButton;
    @FXML private Button swapButton;

    // @FXML private Text textSample;
    @FXML private VBox textVbox;
    @FXML private Text textSample;
    private MyText myText = new MyText("");
    @FXML private CheckBox boldFont;
    @FXML private CheckBox italicFont;
    @FXML private ChoiceBox<String> chooseFonts;
    @FXML private ChoiceBox<String> choosePainter;
    @FXML private Button addLayerButton;
    @FXML private Button deleteLayerButton;
    @FXML private VBox layersVBox;
    @FXML private TextField canvasWidth;
    @FXML private TextField canvasHeight;
    @FXML private HBox statusHbox;
    // Controller variables
    private Image []scrollPaneOriginalImage;
    private Image []scrollPaneProcessedImage;
    private final FileChooser fileChooser = new FileChooser();
    private Stage stage;
    final ToggleGroup filtersGroup = new ToggleGroup();
    // private final ArrayList<Text> texts = new ArrayList<>();
    private boolean bgStatus;
    private Label reporter = new Label("Outside Pane");
    // GraphicsContext sampleGc;
    GridPane layerGrid = new GridPane();  
    // @FXML private TableView layerTable;
    //private MyTable myTable = new MyTable(); 

    @FXML private CheckBox fillBox;
    @FXML private CheckBox strokeBox;
    int canPivot = 0;
    //test
    // ObservableList<MyPane> workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr));
    MyWorkshop workshop = new MyWorkshop();
    // Filter parameter------------------------------------------------------------------------------
    MyFilter myFilter = new MyFilter();
    MyScrollPane myScrollPane;
    // Filter parameter End--------------------------------------------------------------------------

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        
        reporter.setTextFill(Color.WHITE);
        centerGrid.add(reporter, 0, 1);
        //layersVBox.getChildren().add(myTable);
        //layersVBox.getChildren().get(layersVBox.getChildren().indexOf(myTable)).toFront();
        
        // set ImageView size
        // if we use originalImage.setFitWidth();, image may be out of bound. 

        // add list to ChooseFont
        List<String> fonts = Font.getFamilies();
        for(int i=0 ; i<fonts.size() ; i++){ chooseFonts.getItems().add(fonts.get(i));}

        paintBrushColor.setValue(Color.BLACK);
        paintFillColor.setValue(Color.TRANSPARENT);
        fontColor.setValue(Color.BLACK);
        
        String[] painter = {"Null", "Move Node","Pen","Eraser","Line","Oval","Rectangle","Blur"};
        for(int i=0 ; i<painter.length ; i++){choosePainter.getItems().add(painter[i]);}
        choosePainter.setValue("Null");
        // set filters radio buttons group
       
        noneRadio.setToggleGroup(filtersGroup);
        negativeRadio.setToggleGroup(filtersGroup);
        edgeDetectionRadio.setToggleGroup(filtersGroup);
        grayScaleRadio.setToggleGroup(filtersGroup);
        bluringRadio.setToggleGroup(filtersGroup);
        brightnessRadio.setToggleGroup(filtersGroup);
        medianRadio.setToggleGroup(filtersGroup);
        mirrorRadio.setToggleGroup(filtersGroup);
        sharpenRadio.setToggleGroup(filtersGroup);
        embossRadio.setToggleGroup(filtersGroup);
    
        // set filechooser
        final FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        final FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        // final FileChooser.ExtensionFilter extFilterBMP = new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.BMP");
        // final FileChooser.ExtensionFilter extFilterHEIC = new FileChooser.ExtensionFilter("HEIC files (*.heic)", "*.HEIC");

        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        layerNumber.setText(Integer.toString(canPivot+1));
        addLayerButton.setOnAction(e->{
            choosePainter.setValue("Null");
            if(workshop.workSize<=0)
                return;
            workshop.addLayer();
            canPivot++;
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).setCanvas(canPivot);
            layerNumber.setText(Integer.toString(canPivot+1));
            setPaint();
        });
        deleteLayerButton.setOnAction(e->{
            choosePainter.setValue("Null");
            if(workshop.workSize<=0)
                return;
            workshop.deleteLayer(canPivot);
    
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].clearPage();
            canPivot--;
            if(canPivot<=0){
                canPivot=0;
            }
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).setCanvas(canPivot);
            layerNumber.setText(Integer.toString(canPivot+1));
            setPaint();
            
        });
        layerNumber.setOnAction(e -> {
            choosePainter.setValue("Null");
            // canPivot = 1;
            canPivot = Integer.parseInt(layerNumber.getText())-1;
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).setCanvas(canPivot);
            setPaint();
            }
        );
        swapButton.setOnAction(e -> {
            choosePainter.setValue("Null");
            int end = Integer.parseInt(layerSwap.getText())-1;
            workshop.swapCan(canPivot, end);
            workshop.table.get(workshop.workCurr).swap(canPivot, end);
            layerNumber.setText(Integer.toString(end+1));
            layerSwap.setText(Integer.toString(canPivot+1));
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).setCanvas(canPivot);
            setPaint();
            }
        );
    }

    private void setAdjust(){
        System.out.println("setAdjust begin");

        // set Listener
        adjustRotateSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            adjustRotateLabel.setText("Rotate(" + Integer.toString(newValue.intValue()) + " %)");
            // Rotate(theta, x, y, z)
            Rotate rotate = new Rotate(newValue.intValue()-oldValue.intValue());
            rotate.setPivotX(workshop.stack.get(workshop.workCurr).getWidth()/2);
            rotate.setPivotY(workshop.stack.get(workshop.workCurr).getHeight()/2);
            workshop.rotatePane(rotate);
        });
        adjustUDSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            Rotate UD = new Rotate(newValue.intValue()-oldValue.intValue(),Rotate.X_AXIS);
            UD.setPivotY(workshop.stack.get(workshop.workCurr).getHeight()/2);
            adjustUDLabel.setText("Upside Down(" + Integer.toString(newValue.intValue()) + " %)");
            workshop.rotatePane(UD);      
        });
        adjustLtRSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            Rotate flip = new Rotate(newValue.intValue()-oldValue.intValue(),Rotate.Y_AXIS);
            flip.setPivotX(workshop.stack.get(workshop.workCurr).getWidth()/2);
            adjustLtRLabel.setText("LtR(" + Integer.toString(newValue.intValue()) + " %)");
            workshop.rotatePane(flip);
        });
        adjustScalingSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            adjustScalingLabel.setText("Scaling(" + String.valueOf(Math.round(newValue.doubleValue()*10)/10. + ")"));
        });
        adjustBrightnessSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            adjustBrightnessLabel.setText("Brightness(" + Integer.toString(newValue.intValue()) + " %)");
            //workshop.setImageView.getTransforms().add(new Rotate(30, 0, 0));
        });
        // adjustContrastSlider.valueProperty().addListener((ov, oldValue, newValue) -> {adjustContrastLabel.setText("Contrast(" + Integer.toString(newValue.intValue()) + " %)");});
        // adjustColorfulnessSlider.valueProperty().addListener((ov, oldValue, newValue) -> {adjustColorfulnessLabel.setText("Colorfulness(" + Integer.toString(newValue.intValue()) + " %)");});
        adjustResetButton.setOnAction(e->{
            workshop.clearRotate();
            resetAdSlider();
        });

        adjustBlurSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            workshop.setImageView(myFilter.getFilter_Bluring(newValue.intValue()).getImageView());
            adjustBlurLabel.setText("Blur(" + Integer.toString( newValue.intValue() ) + " %)");
        });
        adjustAlphaSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            workshop.setImageView(myFilter.getFilter_Alpha(newValue.intValue()).getImageView());
            adjustAlphaLabel.setText("Alpha(" + Integer.toString( (int)(newValue.intValue()/255.*100) ) + " %)");
        });
        adjustBrightnessSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            workshop.setImageView(myFilter.getFilter_Brightness(newValue.intValue()).getImageView());
            adjustBrightnessLabel.setText("Brightness(" + Integer.toString( newValue.intValue() ) + " %)");
        });

        System.out.println("setAdjust end");
    }
    private void setPaint(){
        System.out.printf("Paint begin %d,", canPivot);
        // clear
        clearPaintButton.setOnAction(e -> {
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].clearPage();
        }
            );

        // size
        setPaintBrushSizeSlider.setValue(3);
        setPaintBrushSizeSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            brushSizeLabel.setText("Size(" + Integer.toString(newValue.intValue()) + ")");
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setLineWidth(newValue.intValue());
            sampleBrush.setStrokeWidth(newValue.intValue());
        });

        // Fill color
        workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setFill(paintFillColor.getValue());
        sampleBrush.setFill(paintFillColor.getValue());
        fillBox.setSelected(true);
        paintFillColor.setOnAction(e ->{
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setFill(paintFillColor.getValue());
            sampleBrush.setFill(fillBox.isSelected() ? paintFillColor.getValue() : Color.TRANSPARENT);
        });
        fillBox.setOnAction(e -> {
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setFill(paintFillColor.getValue());
            sampleBrush.setFill(fillBox.isSelected() ? paintFillColor.getValue() : Color.TRANSPARENT);
        });  

        //stroke      
        strokeBox.setSelected(true);
        workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setStroke(paintBrushColor.getValue());
        sampleBrush.setStroke(paintBrushColor.getValue());
        paintBrushColor.setOnAction(e -> {
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setStroke(strokeBox.isSelected() ? paintBrushColor.getValue() : Color.TRANSPARENT);
            sampleBrush.setStroke(paintBrushColor.getValue());
        });
    
        strokeBox.setOnAction(e -> {
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setStroke(strokeBox.isSelected() ? paintBrushColor.getValue() : Color.TRANSPARENT);
            sampleBrush.setStroke(strokeBox.isSelected() ? paintBrushColor.getValue() : Color.TRANSPARENT);
        });
        
        // painter kinds
        choosePainter.setOnAction(e -> {
                if(choosePainter.getValue().equals("Move Node")){
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).toBack();
                }
                else if(choosePainter.getValue().equals("Null")){
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].setStroke(strokeBox.isSelected() ? paintBrushColor.getValue() : Color.TRANSPARENT);
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].myBrush.setMode(0);
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).toFront();
                }
                else if(choosePainter.getValue().equals("Pen")){
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).toFront();
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].myBrush.setMode(1);
                }
                else if(choosePainter.getValue().equals("Eraser")){
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).toFront();
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].myBrush.setMode(2);
                }
                else if(choosePainter.getValue().equals("Line")){
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).toFront();
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].myBrush.setMode(3);
                }
                else if(choosePainter.getValue().equals("Oval")){
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).toFront();
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].myBrush.setMode(4);

                }
                else if(choosePainter.getValue().equals("Rectangle")){
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).toFront();
                    workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).myCanvas[canPivot].myBrush.setMode(5);
                }

            }
        );
    
        System.out.println("Paint end");
    }

    private void setText(){
        System.out.println("setText begin");
        textSample.setFont(Font.font (chooseFonts.getValue(),Integer.parseInt(setTextSize.getText().toString())));

        setTextSize.textProperty().addListener((ov, oldValue, newValue) -> {
            myText.updateFontStyle(textSample, chooseFonts.getValue(), boldFont.isSelected(), italicFont.isSelected(), Integer.parseInt(setTextSize.getText().toString()));
            
        });

        // set Font
        chooseFonts.setValue("Times New Roman");
        myText.updateFontStyle(textSample, chooseFonts.getValue(), boldFont.isSelected(), italicFont.isSelected(), Integer.parseInt(setTextSize.getText().toString()));
        chooseFonts.setOnAction((e) -> {myText.updateFontStyle(textSample, chooseFonts.getValue(), boldFont.isSelected(), italicFont.isSelected(), Integer.parseInt(setTextSize.getText().toString()));});
        // set color picker default color
        fontColor.setOnAction(e ->{textSample.setFill(fontColor.getValue());});
        boldFont.setOnAction(e ->{myText.updateFontStyle(textSample, chooseFonts.getValue(), boldFont.isSelected(), italicFont.isSelected(), Integer.parseInt(setTextSize.getText().toString()));});
        italicFont.setOnAction(e ->{myText.updateFontStyle(textSample, chooseFonts.getValue(), boldFont.isSelected(), italicFont.isSelected(), Integer.parseInt(setTextSize.getText().toString()));});    
        System.out.println("setText end");
    }
    
    @FXML
    private void CreateText(ActionEvent event) {
        if(workshop.workSize<=0)
            return;
        // System.out.println(inputText.getText().toString());
        
        if(Integer.parseInt(setTextSize.getText()) > 0 && !inputText.getText().equals("")){
            workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr)).addText(inputText.getText().toString(), chooseFonts.getValue(), boldFont.isSelected(), 
            italicFont.isSelected(), Integer.parseInt(setTextSize.getText().toString()), fontColor.getValue(), workshop.nodeGestures.get(workshop.workCurr));
            //System.out.printf("%d %d",workshop.workCurr, workshop.current.get(workshop.workCurr));
            //workshop.pane.get(workshop.workCurr).get(workshop.current.get(workshop.workCurr));
            choosePainter.setValue("Move Node");
            //MyPane.toFront();
        }
        
    }
    private Stage getStage() {
        if (stage == null) {
            stage = (Stage) mainPane.getScene().getWindow();
        }
        return stage;
    }
    @FXML
    private void paperButtonPressed(){
        mainPane.getChildren().remove(myScrollPane);
        if(workshop.workSize>0){
            layersVBox.getChildren().remove(1);
            workshop.clearAll();
            
            ObservableList<Node> childrens = centerGrid.getChildren();
            for(Node node : childrens) {
                if(node instanceof StackPane && centerGrid.getRowIndex(node) == 0 && centerGrid.getColumnIndex(node) == 0) {
                    StackPane stack = (StackPane) (node); // use what you want to remove
                    centerGrid.getChildren().remove(stack);
                    break;
                }
            }
        }
        
        bgStatus = false;
        workshop.newWorkshop(Double.parseDouble(canvasWidth.getText().toString()), Double.parseDouble(canvasHeight.getText().toString()), "new paper");
        centerGrid.add(workshop.stack.get(workshop.workCurr), 0, 0);
        centerGrid.setHalignment(workshop.stack.get(workshop.workCurr), HPos.CENTER);
        layersVBox.getChildren().add(workshop.table.get(workshop.workCurr));
        layersVBox.getChildren().get(layersVBox.getChildren().indexOf(workshop.table.get(workshop.workCurr))).toBack();
        workshop.getPositionLabel(reporter);
        
        resetInitial();
        setPaint();
        setText();
        setAdjust();
        
    }
    @FXML
    private void ImportButtonPressed(final ActionEvent event) {
        // set stage
        this.stage = getStage();
        // open fileChooser
        fileChooser.setTitle("Open Resource File");
        final List<File> list = fileChooser.showOpenMultipleDialog(stage);

        // import image
        if (list != null) {
            
            if(workshop.workSize>0){
                layersVBox.getChildren().remove(1);
                workshop.clearAll();
                ObservableList<Node> childrens = centerGrid.getChildren();
                for(Node node : childrens) {
                    if(node instanceof StackPane && centerGrid.getRowIndex(node) == 0 && centerGrid.getColumnIndex(node) == 0) {
                        StackPane stack = (StackPane) (node); // use what you want to remove
                        centerGrid.getChildren().remove(stack);
                        break;
                    }
                }
            }
            //---------
            Image []image=new Image[list.size()];
            scrollPaneOriginalImage=new Image[list.size()];
            scrollPaneProcessedImage=new Image[list.size()];
            for(int i=0;i<list.size();i++){
                image[i]=new Image (list.get(i).toURI().toString());
                scrollPaneOriginalImage[i]=image[i];
                scrollPaneProcessedImage[i]=image[i];
            }
            myScrollPane=new MyScrollPane(scrollPaneProcessedImage,list.size(),myFilter);
            mainPane.setLeft(myScrollPane);
            //---------
            //Image image = new Image(file.toURI().toString());
            workshop.newWorkshop(image[0].getWidth(), image[0].getHeight(), list.get(0).getName());
            workshop.addImage(image[0]);
            centerGrid.add(workshop.stack.get(workshop.workCurr), 0, 0);
            centerGrid.setHalignment(workshop.stack.get(workshop.workCurr), HPos.CENTER);
            myFilter.setMyFilter(scrollPaneProcessedImage[0]);
            workshop.getPositionLabel(reporter);
            canvasWidth.setText(Double.toString(image[0].getWidth()));
            canvasHeight.setText(Double.toString(image[0].getHeight()));
            layersVBox.getChildren().add(workshop.table.get(workshop.workCurr));
            layersVBox.getChildren().get(layersVBox.getChildren().indexOf(workshop.table.get(workshop.workCurr))).toFront();
            bgStatus = true;

            // myFilter.setMyFilter(scrollPaneProcessedImage[0]);
            //processedScreen.getChildren().add(processedImageView.imageView);
        }
        else{System.out.println("user didn't select image");}
        resetInitial();
        setPaint();
        setText();
        setAdjust();
    }
    
    private void resetInitial(){
        //reset initial 
        brushSizeLabel.setText("Size(3)");
        choosePainter.setValue("Null");
        filtersGroup.selectToggle(null);
        resetAdSlider();
        inputText.setText("");
        setTextSize.setText("12");
        paintBrushColor.setValue(Color.BLACK);
        paintFillColor.setValue(Color.TRANSPARENT);
        fontColor.setValue(Color.BLACK);
        if(workshop.workSize<=0)
            return;
        workshop.clearRotate();
    }
    private void resetAdSlider() {
        adjustRotateSlider.setValue(0);
        adjustUDSlider.setValue(0);
        adjustLtRSlider.setValue(0);
        adjustBrightnessSlider.setValue(0);
        adjustBlurSlider.setValue(0);
        adjustScalingSlider.setValue(1);
    }
    @FXML
    private void DownloadButtonPressed(final ActionEvent event) {
        // open fileChooser
        if(workshop.workSize<=0){
            return;
        }
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(stage);

        //save for screen shoot
        if(file != null){
            workshop.screenshot(file, bgStatus, adjustScalingSlider.getValue());
        }
    }

    // Filter method------------------------------------------------------------------------------
    @FXML
    private void RadioButtonChoosed(final ActionEvent event){
        if(workshop.workSize<=0)
            return;
        if(filtersGroup.getSelectedToggle()==noneRadio){workshop.setImageView(myFilter.getFilter_None().getImage());}
        else if(filtersGroup.getSelectedToggle()==negativeRadio){workshop.setImageView(myFilter.getFilter_Negative().getImageView());}
        else if(filtersGroup.getSelectedToggle()==edgeDetectionRadio){workshop.setImageView(myFilter.getFilter_EdgeDetecting().getImageView());}
        else if(filtersGroup.getSelectedToggle()==grayScaleRadio){workshop.setImageView(myFilter.getFilter_GrayScale().getImageView());}
        else if(filtersGroup.getSelectedToggle()==bluringRadio){workshop.setImageView(myFilter.getFilter_Bluring(4).getImageView());}
        else if(filtersGroup.getSelectedToggle()==brightnessRadio){workshop.setImageView(myFilter.getFilter_Brightness(50).getImageView());}
        else if(filtersGroup.getSelectedToggle()==medianRadio){workshop.setImageView(myFilter.getFilter_Median().getImageView());}
        else if(filtersGroup.getSelectedToggle()==mirrorRadio){workshop.setImageView(myFilter.getFilter_Mirror().getImageView());}
        else if(filtersGroup.getSelectedToggle()==sharpenRadio){workshop.setImageView(myFilter.getFilter_Sharpen().getImageView());}
        else if(filtersGroup.getSelectedToggle()==embossRadio){workshop.setImageView(myFilter.getFilter_Emboss().getImageView());}
    }
    // Filter method end--------------------------------------------------------------------------
}