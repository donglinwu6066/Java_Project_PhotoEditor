https://youtu.be/06JXJs9Ai-M

# Compile parameter
>Javac -d bin --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing src/*.java

# Run
>Java --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing -classpath bin PhotoEditor


# Markdown Reference
* [Markdown - 易編易讀，優雅的寫文吧！](https://ithelp.ithome.com.tw/articles/10203758)  
* [十分鐘快速掌握 Markdown](https://wcc723.github.io/development/2019/11/23/ten-mins-learn-markdown/)

# Demo2
> 2020/5/28
1. 單次載入多圖片編輯
2. 去背
3. 模糊工具
4. 顏色取樣
5. 網路抓圖


# Demo
> 2020/5/14 Demo
1. 發現painter的bug 
2. 新圖原圖中間要加分隔線
3. 伸縮圖片
4. 透明背景
5. 參考photoshop

# Update

>Wu 2020/5/8
### 附註
1. 修正rotate、Flip圖片的bug，更改圖片的載入大小
>Wu 2020/5/3
### 附註
1. 正式以bin、src、image分裝檔案bin下設resources檔
2. bin放置.class檔與resources檔，resources裝fxml與css，src裝.java，image是裝我們要處理的相片
3. .classpath 檔是eclipse專案使用，我們不需要
4. resources檔(放置.fxml與.css)放在bin下面，原因是`getClass().getResource("resources/PhotoEditor.fxml");好像無法直接用"../"去回到parent directory
5. run參數中-classpath bin是提醒終端機class檔的放置處，與實際的class檔中間要有一個空格，如: -classpath bin PhotoEditor

>Wu 2020/5/2
### 附註
1. 這次將Painter大致完成  
2. 筆刷的部分是用Polymorphism完成，加上了資調夾，但因為我找不到好的封包方法，所以是將資料夾內的東西編譯到目前資料夾，**故編譯要多加編譯到目前資料夾"-d ." 與資料夾"brushtool/*.java"**(沒更改程式碼可省略)  
3. 畫布`Canvas`有一個暫時的`TmpPaintCanvas`，是用來繪製如直線，矩形等效果預覽，放開滑鼠後，會繪製到正確畫布。  
4. 畫布可以不用把每個點都存下來重畫，那樣程式會變得很重很慢，所以我捨去了，至於只擦掉一條線目前是沒有考慮，因為這樣好像要重新把點存起來用Listener  
5. 筆跡優化是用模糊`BlurBox`效果，但會造成`gc.clearRect()`失去效用，所以清除畫面前都要先取消，待`gc.clearRect()`後再重新補上  
6. 橡皮擦部分無法連續，雖然可以藉由調很大來創造連續的幻覺，但我想保留調整大小的功能，Demo時請擦慢一點，以免破功  
7. 橡皮擦在白色畫布時可以瞬間更改成白色來覆蓋原筆跡，待release後再射回原來顏色，但彩色背景沒轍  
8. 開啟Image的大小我已經優化過了，會依照寬或高來選擇最大開啟。
9. 圖層之間是有上下關係的，我沒找到變透明的方法，請務必先把PaintCanvas放在最上面(Scene Builder列表中StackPane的最下面)  
10. 用`setOnAction`與Lambda expression比Function還簡短多了，如果程式1~2行推薦使用  
11.  因為程式碼逐漸變長，我重新拆分`public void initialize()`內的分布，以method分成**Filters**，**Adjust**，**Paint Brush**，**Text**，**Image**的部分來呼叫  

### 問題與下次目標
1. 我仍然無法理解如何正確用package引入檔案  
2. Pen在調整透明度後，即便用lineTo仍然無法改善期重疊效果，目前只支援除了Pen以外的作圖
3. Rotate相片目前我只能旋轉或鏡射擇一，應該是要用`ImageView.getTransforms().add()`來優化，之後再看看狀況 
4. 過大的`Canvas`會造成筆跡歪掉，意即落筆處予顯示處不同，但似乎無法重新設定`Canvas`大小，所以我直接用定值去設定，之後希望能變成自適大小 
5. 有看到3D的建模，跟2D好像沒有差很多，不知道能不能引入(如果有時間)   
6. 下一個目前在研究Drag and Drop功能，  

>Wu 2020/5/1
### 附註
1. 這裡我大致完成工具列基本顯示與連結，包含Slider與Label之間的連結等  
2. Label是用來顯示純文字的，Text是用來顯示藝術文字的，效果不一樣  
3. 因為修圖區有很多個圖層，無法直接下載`ImageView`，所以我是以snapshoot截圖，優點是多圖層合一，**缺點是降低畫素**
4. snapshoot要記得把背景調成透明，否則會白色顯示

>Wu 2020/4/30~before
### 附註
1. 這裡捨去了可能擁有的一大堆fxml，改採用titled Pane
2. 如果要在Controller內呼叫另一個fxml，要先接下stage
``` java
    private Stage getStage() {
        if (stage == null) {
            stage = (Stage) MainPane.getScene().getWindow();
        }
        return stage;
    }
```
再來呼叫有**throws IOException**的函式，例如:
``` java
    @FXML
    private void FiltersButtonPressed(final ActionEvent event) throws IOException {
        
        //second window (Parent root1 = (Parent) fxmlLoader.load();) require "throws IOException"
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FiltersPanel.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Filters Panel");
        stage.setScene(new Scene(root));  
        stage.show();
        
    }
```
3. 存檔時用的`ImageIO.write()`是javax.imageio.ImageIO的東西，所以我們ImageView出來的檔案要轉型，正常我們是使用`SwingFXUtils.fromFXImage()`，所以編譯時需要加上參數javafx.swing才能通過，完整程式碼大概像這樣
``` java
ImageIO.write(SwingFXUtils.fromFXImage(ProcessedImage.getImage(),null), "png", file);
```
# Note
## Wu
* 這個Markdown 是我一些小筆記，懶得記載可以跳過  
* 這次我在命名上有失誤，variables應該要小寫開頭，但我目前懶得改了
* 第一次Demo時一定要降階Demo，為期末考爭取緩衝
* package設定時資料夾全部小寫，最後不需要.*
* 第4版以前(未正式分裝檔案)編譯指令"Javac -d . --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing brushtool/*.java *.java"，執行指令"Java -d . --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing PhotoEditor"
* README的版本編號與雲端上的版本不一樣，會比較多
* 編譯請在Project這個資料夾編譯

# Reference
* [圖片處理種類介紹](http://www.csie.ntnu.edu.tw/~u91029/Image.html)  
* [Take a snapshot with JavaFX!](https://blogs.oracle.com/thejavatutorials/take-a-snapshot-with-javafx)  


Javac -d bin --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing src/*.java
Java --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.swing -classpath bin PhotoEditor
