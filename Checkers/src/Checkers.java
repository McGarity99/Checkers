
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Arrays;
import java.util.Optional;

import javafx.application.Application;

public class Checkers extends Application {
	
	// Globals
	EventHandler<ActionEvent> exit;
	EventHandler<ActionEvent> infoEvent;
    HBox topMenu;		// menu at top of the UI
    HBox turnTracker;	// tracker for current player turn
    Button exitButton;	// button to exit the game
	Button infoButton; // button to display additional info
    VBox root;			// root of the UI
	Alert infoBox;		// information box (author, version, etc.)
	Alert victoryAlert; // notify the winning player that they've won
	Alert playAgainAlert; // ask the players if they want to play again
    
    boolean aiEnabled = false;
    
    Image redSpaceBlank = new Image("resources/redSpace.jpg");
    Image blackSpaceBlank = new Image("resources/blackSpace.jpg");
    Image whitePiece = new Image("resources/whitePiece.jpg");
    Image whitePieceSelected = new Image("resources/whitePieceSelected.jpg");
    Image redPiece = new Image("resources/redPiece.jpg");
    Image redPieceSelected = new Image("resources/redPieceSelected.jpg");
    Image whiteKing = new Image("resources/whiteDawg.jpg");
    Image whiteKingSelected = new Image("resources/whiteDawgSelected.jpg");
    Image redKing = new Image("resources/redDawg.jpg");
    Image redKingSelected = new Image("resources/redDawgSelected.jpg");
    Image checkersLogo = new Image("resources/UGA Face.jpg");
    ButtonType exitDialog = new ButtonType("EXIT");
    
    GridPane mainPane = new GridPane();
    
    int redCount;
    int whiteCount;
	int jumpedRow;
	int jumpedCol;

	boolean game_over = false;
    
    Text redTracker = new Text("Red Count: " + redCount);
    Text whiteTracker = new Text("White Count: " + whiteCount);
    Text currentColor = new Text("red"); // red player will always have first turn
    Text colorIndicator = new Text("Current Turn: " + currentColor.getText());
    //Text status = new Text("Player's Turn");
    
    ImageView[][] imageGrid = new ImageView[8][8];
    ImageView redSpaceExample = new ImageView(redSpaceBlank);
    ImageView blackSpaceExample = new ImageView(blackSpaceBlank);
    ImageView redPieceExample = new ImageView(redPiece);
    ImageView whitePieceExample = new ImageView(whitePiece);
    ImageView redKingExample = new ImageView(redKing);
    ImageView whiteKingExample = new ImageView(whiteKing);
    ImageView theLogo = new ImageView(checkersLogo);
    
    int selectedRow; // row of selected piece (to move)
    int selectedCol; // col of selected piece (to move)
    
    int targetRow; // row of target space (to move to)
    int targetCol; // col of target space (to move to)
    
    int row; // for board setup
    int col; // for board setup
    
    Checkers nextGame;
    Scene mainScene;
    Stage mainStage;

	enum gradient_direction {
		RIGHT, LEFT, UP, DOWN
	}
	
	@Override
	public void start(Stage stage) {
		reset();
		/* setupOther();
		//Optional<ButtonType> r = aiQuery.showAndWait(); // return to this later
		//if (r.toString().equals("Optional[ButtonType [text=Yes, buttonData=OTHER]]")) {
	    //	   aiEnabled = true;
	    //} else aiEnabled = false;
		for (row = 0; row < 8; row++) {
			for (col = 0; col < 8; col++) {
				// need to re-initialize these on each iteration to avoid duplicate child nodes
				ImageView redTempView = new ImageView(redSpaceBlank);
				redTempView.setPreserveRatio(true);
				redTempView.setFitWidth(80.0);
				redTempView.setFitHeight(80.0);
				redTempView.setOnMouseClicked(event -> {
					// leave blank for now (only playing on black squares)
				});
				ImageView blackTempView = new ImageView(blackSpaceBlank);
				blackTempView.setPreserveRatio(true);
				blackTempView.setFitWidth(80.0);
				blackTempView.setFitHeight(80.0);
				blackTempView.setOnMouseClicked(event -> {
					imageEventHandler(blackTempView);
					checkForEnd();
				});
				
				// if-else to determine placement of red/black squares on the board
				if (row % 2 == 0) { // if filling in an "even" row (fill blacks on odd indices)
					switch(col % 2) {
					case 0:
						imageGrid[row][col] = redTempView;
						mainPane.add(redTempView, col, row);
						break;
					default:
						imageGrid[row][col] = blackTempView;
						mainPane.add(blackTempView, col, row);
					}
					initializeCoordinates(row, col);
				} else { // if filling in an "odd" row (fill blacks on even indices)
					switch(col % 2) {
					case 0:
						imageGrid[row][col] = blackTempView;
						mainPane.add(blackTempView, col, row);
						break;
					default:
						imageGrid[row][col] = redTempView;
						mainPane.add(redTempView, col, row);
					}
					initializeCoordinates(row, col);
				}
			} // inner for
		} // outer for
		root.getChildren().addAll(topMenu, mainPane, turnTracker);
		setupScene(); */
	}
	
	/*
	 * Handle all potential actions for selecting a game piece
	 * 
	 * Select first piece: if coordinates are unset and the space has an image and it is not selected and it is the same color as the current color
	 * 	THEN -> set coordinates
	 * 
	 * Change selection: if space has an image and it is not selected and it is the same color as the current color
	 * 	THEN -> reset iamge of coordinates, set new coordinates
	 * 
	 * Destination selection: if coordinates are set and new space is selected and is blank and is a valid move
	 * THEN -> move the piece
	 */
	
	public void imageEventHandler(ImageView iv) {
		//System.out.println("entering event handler");
		if (selectedRow == -1 && selectedCol == -1 && colorMatches(iv) && !isSelected(iv)) { // if selecting piece to move
			//System.out.println("selecting first coordinates");
			selectedRow = GridPane.getRowIndex(iv);
			selectedCol = GridPane.getColumnIndex(iv);
			setSelectedImage(selectedRow, selectedCol, false);
			
		} else if ((selectedRow != -1 && selectedCol != -1) && (hasImage(iv) && !isSelected(iv) && colorMatches(iv))) { // if changing selection to move
			//System.out.println("re-selecting first coordinates");
			setSelectedImage(selectedRow, selectedCol, true); // reset previous selection's Image
			selectedRow = GridPane.getRowIndex(iv);
			selectedCol = GridPane.getColumnIndex(iv);
			setSelectedImage(selectedRow, selectedCol, false); // select the new space
			
		} else if ((selectedRow != -1 && selectedCol != -1)) { // if selecting destination space
			//System.out.println("selecting destination space");
			if (hasImage(imageGrid[selectedRow][selectedCol])) {
				//System.out.println("selected space is blank");
				//System.out.println("Initial: " + selectedRow + ", " + selectedCol);
				targetRow = GridPane.getRowIndex(iv);
				targetCol = GridPane.getColumnIndex(iv);
				boolean jumping = isJumpingMove(selectedRow, selectedCol, targetRow, targetCol);
				if (isBlank(imageGrid[targetRow][targetCol]) && validMove(selectedRow, selectedCol, targetRow, targetCol, jumping)) {
					// we want to log the image value of the moving piece, then move it (set its image value at the destination space), then set the original space to a blank image
					Image pieceImage = imageGrid[selectedRow][selectedCol].getImage(); // image value of the moving piece

					if (targetRow == 0 && deselectImage(pieceImage) == redPiece) {
						imageGrid[targetRow][targetCol].setImage(redKing);
					} else if (targetRow == 7 && deselectImage(pieceImage) == whitePiece) {
						imageGrid[targetRow][targetCol].setImage(whiteKing);
					} else imageGrid[targetRow][targetCol].setImage(deselectImage(pieceImage));

					imageGrid[selectedRow][selectedCol].setImage(blackSpaceBlank);

					selectedRow = -1;
					selectedCol = -1;
					if (jumpedRow != -1 && jumpedCol != -1) { // remove the jumped piece from the board
						imageGrid[jumpedRow][jumpedCol].setImage(blackSpaceBlank);
						jumpedRow = -1;
						jumpedCol = -1;
					}
				} //else //System.out.println("Destination was not blank or is invalid move");
			}
		}
	}
	
	/*
	 * Take in an Image value, and if it is of a "selected" state, return its non-selected equivalent
	 */
	
	public Image deselectImage(Image im) {
		if (im == redPieceSelected) {
			return redPiece;
		} else if (im == redKingSelected) {
			return redKing;
		} else if (im == whitePieceSelected) {
			return whitePiece;
		} else if (im == whiteKingSelected) {
			return whiteKing;
		} else return im;
	}
	
	/*
	 * Return true if the given ImageView has an Image property representing a game piece
	 * AND that game piece's color matches the color of the current player's turn. Return false otherwise.
	 */
	
	public boolean colorMatches(ImageView iv) {
		boolean result = false;
		if (hasImage(iv)) {
			if (currentColor.getText().equals("red") && isRedPiece(iv)) {
				result = true;
			} else if (currentColor.getText().equals("white") && !isRedPiece(iv)) {
				result = true;
			}
		}
		return result;
	}
	
	/*
	 * When a piece is selected on the board, set its image to visually indicate it has been selected.
	 * If a piece has already been selected, then reset the image of the previously-selected piece.
	 */
	public void setSelectedImage(int row, int col, boolean resetting) {
		//System.out.println("setSelectedImage row: " + row);
		//System.out.println("col: " + col);
		//System.out.println("resetting: " + resetting);
		Image image_type = imageGrid[row][col].getImage();
		if (image_type == whitePiece || image_type == whitePieceSelected) {
			//System.out.println("white piece select");
			if (image_type == whitePieceSelected && resetting) {
				imageGrid[row][col].setImage(whitePiece);
			} else imageGrid[row][col].setImage(whitePieceSelected);
		} else if (image_type == whiteKing || image_type == whiteKingSelected) {
			//System.out.println("white king select");
			if (image_type == whiteKingSelected && resetting) {
				imageGrid[row][col].setImage(whiteKing);
			} else imageGrid[row][col].setImage(whiteKingSelected);
		} else if (image_type == redPiece || image_type == redPieceSelected) {
			//System.out.println("red piece select");
			if (image_type == redPieceSelected && resetting) {
				imageGrid[row][col].setImage(redPiece);
			} else imageGrid[row][col].setImage(redPieceSelected);
		} else {
			//System.out.println("red king select");
			if (image_type == redKingSelected && resetting) {
				imageGrid[row][col].setImage(redKing);
			} else imageGrid[row][col].setImage(redKingSelected);
		}
	}
	
	/*
	 * Call this method at launch to set up various UI components
	 */
	public void setupOther() {
		Background filler = new Background(new BackgroundFill(Color.BLACK, null, null));
		infoBox = new Alert(AlertType.INFORMATION, "Checkers | v1.0.0\nAuthor: Hunter McGarity");
    	
    	topMenu = new HBox(20);
        topMenu.setBackground(filler);
		topMenu.setAlignment(Pos.BASELINE_CENTER);
        exit = event -> mainStage.close();
        exitButton = new Button("EXIT");
        exitButton.setOnAction(exit);
		format_button(exitButton, gradient_direction.DOWN);
        redTracker.setFill(Color.RED);
        whiteTracker.setFill(Color.ANTIQUEWHITE);
		infoButton = new Button("Info");
		infoEvent = event -> infoBox.showAndWait();
		infoButton.setOnAction(infoEvent);
		format_button(infoButton, gradient_direction.UP);
        topMenu.getChildren().addAll(exitButton, redTracker, whiteTracker, infoButton);
        
        turnTracker = new HBox();
        turnTracker.setBackground(filler);
        //status.setFill(Color.ANTIQUEWHITE);
        //turnTracker.getChildren().add(status);
        turnTracker.setAlignment(Pos.BASELINE_CENTER);
        
        colorIndicator.setFill(Color.ANTIQUEWHITE);
        colorIndicator.setUnderline(true);
        colorIndicator.setFill(Color.RED);
		turnTracker.getChildren().add(colorIndicator);

        root = new VBox();
	}

	private void format_button(Button b, gradient_direction dir) {
		b.setMinWidth(120.0);
		b.setMaxWidth(240.0);
		b.setPrefWidth(120.0);
		b.setTextFill(Color.ANTIQUEWHITE);
		//b.setStyle("-fx-background-color: #000000; -fx-border-color: #ff0000");
		String style_str = "-fx-background-color: linear-gradient(red, black); -fx-border-color: #ffffff";
		int anchor = style_str.indexOf("(");
		switch(dir) {
			case UP:
				style_str = style_str.substring(0, anchor + 1) + "to top, " + style_str.substring(anchor + 1);
				break;
			case DOWN:
				style_str = style_str.substring(0, anchor + 1) + "to bottom, " + style_str.substring(anchor + 1);
				break;
			case LEFT:
				style_str = style_str.substring(0, anchor + 1) + "to left, " + style_str.substring(anchor + 1);
				break;
			case RIGHT:
				style_str = style_str.substring(0, anchor + 1) + "to right, " + style_str.substring(anchor + 1);
				break;
		}
		b.setStyle(style_str);
	}
	
	/**
     * This method sets up necessary components of the Scene graph,
     * namely the Scene itself, and calls necessary methods to size the
     * stage to the scene, show the stage, and set the title of the Stage.
     */
	private void setupScene() {
    	mainStage = new Stage();
        mainScene = new Scene(root);
        mainStage.setTitle("Checkers App");
        mainStage.setScene(mainScene);
        mainStage.show();
        mainStage.sizeToScene();
    } //setUpScene
	
	/*
	 * Handle the setting of initial white/red pieces.
	 * I broke this out into its own method from @code{start} to de-bloat the loop
	 * code in that method.
	 * 
	 * If a given set of coordinates does not meet criteria for an initial piece, this method
	 * does nothing.
	 */
	public void initializeCoordinates(int row, int col) {
		////System.out.println("initializing with row: " + row + " | col:" + col);
		int[] validColsEven = {1, 3, 5, 7};
		int[] validColsOdd = {0, 2, 4, 6};
		if ((row % 2 == 0) && row < 3) { // even row, white
			if (arrContains(validColsEven, col)) {
				imageGrid[row][col].setImage(whitePiece);
			}
			
		} else if ((row % 2 == 0) && row > 4) { // even row, red
			if (arrContains(validColsEven, col)) {
				imageGrid[row][col].setImage(redPiece);
			}
		} else if ((row % 2 != 0) && row < 3) { // odd row, white
			if (arrContains(validColsOdd, col)) {
				imageGrid[row][col].setImage(whitePiece);
			}
		} else if ((row % 2 != 0) && row > 4) { // odd row, red
			if (arrContains(validColsOdd, col)) {
				imageGrid[row][col].setImage(redPiece);
			}
		}
	}
	
	public boolean arrContains(int[] arr, int target) {
		return Arrays.stream(arr).anyMatch(n -> n == target);
	}
	
	/*
	 * Return true if the given ImageView has an Image property representing a red game piece, false otherwise
	 */
	
	public boolean isRedPiece(ImageView iv) {
		Image image_val = iv.getImage();
		return (image_val == redPiece || image_val == redKing || image_val == redPieceSelected || image_val == redKingSelected); 
	}
	
	/*
	 * Return true if given ImageView is a king piece (selected or not).
	 */
	
	public boolean isKing(ImageView iv) {
		Image image_val = iv.getImage();
		return (image_val == redKing || image_val == redKingSelected || image_val == whiteKing || image_val == whiteKingSelected);
	}
	
	// Return true if given ImageView has a game piece on it
	public boolean hasImage(ImageView iv) {
		Image image_val = iv.getImage();
		return (image_val != redSpaceBlank && image_val != blackSpaceBlank);
	}
	
	// Return true if the given ImageView is a valid blank space (black)
	public boolean isBlank(ImageView iv) {
		Image image_val = iv.getImage();
		return (image_val == blackSpaceBlank);
	}
	
	/*
	 * Looking at the original space and the destination space,
	 * determine if this intended move is going to be a jump move or not.
	 * This is needed for correct logic around determining the validity of the move later.
	 */
	
	public boolean isJumpingMove(int origRow, int origCol, int destRow, int destCol) {
		
		boolean jumping = false;
		
		// If the space difference is only one space, it is not a jump
		if ((Math.abs(origRow - destRow) == 1) && (Math.abs(origCol - destCol) == 1)) {
			return false;
		}
		
		// Since each turn can only jump one space, a valid jump move will only move +- 2 on the row and +- 2 on the col
		if ((Math.abs(destRow - origRow) == 2) && (Math.abs(destCol - origCol) == 2)) {
			int opponentRow;
			int opponentCol;
			// determine direction of the jump and where the opponent piece should be
			if (destRow > origRow && destCol > origCol) { // down-right
				opponentRow = destRow - 1;
				opponentCol = destCol - 1;
			} else if (destRow > origRow && destCol < origCol) { // down-left
				opponentRow = destRow - 1;
				opponentCol = destCol + 1;
			} else if (destRow < origRow && destCol > origCol) { // up-right
				opponentRow = destRow + 1;
				opponentCol = destCol - 1;
			} else { // up-left
				opponentRow = destRow + 1;
				opponentCol = destCol + 1;
			}
			if (!colorMatches(imageGrid[opponentRow][opponentCol])) { // found the opponent piece
				jumping = true;
			}
		}
		return jumping;
	}
	
	/*
	 * Return true if the given ImageView has a "selected" Image property, false otherwise.
	 */
	
	public boolean isSelected(ImageView iv) {
		Image image_val = iv.getImage();
		return (
				image_val == redPieceSelected
				|| image_val == redKingSelected
				|| image_val == whitePieceSelected
				|| image_val == whiteKingSelected
				);
	}
	
	/*
	 * Based on the current turn, check if an intended piece move is valid or not.
	 * Call the appropirate checking method based on current turn, and return true only if the move is valid.
	 */
	
	public boolean validMove(int origRow, int origCol, int destRow, int destCol, boolean isJumping) {
		if (currentColor.getText().equals("red")) {
			return validMoveRed(origRow, origCol, destRow, destCol, isJumping);
		} else return validMoveWhite(origRow, origCol, destRow, destCol, isJumping);
	}
	
	/* Return true if the selected move (from orig to dest) is valid based on piece placement & color
	 Criteria for valid move:
		coordinates must be at least 2 tiles removed from the orig coordinates
		coordinates must point to a blank square on the board with a piece of opposite color in between
	*/
	public boolean validMoveRed(int origRow, int origCol, int destRow, int destCol, boolean isJumping) {		
		boolean validCoordinates = false;
		
		// forward coordinates for red means up and to left/right
		// for king piece, up/down and left/right
		// Check if coordinates are in the "right" direction
		// NOTE: a row/col difference being 1 only applies if the piece is moving but not jumping an opponent piece
		if (isKing(imageGrid[origRow][origCol])) {
			if ((Math.abs(destRow - origRow) == 1) && (Math.abs(destCol - origCol) == 1)) { // row difference is 1, col difference is 1
				validCoordinates = true;
				currentColor.setText("white"); // no piece jumped, shift to other player
				updateTurnIndicator();
			} else if ((Math.abs(destRow - origRow) == 2) && (Math.abs(destCol - origCol) == 2) && interveningPiece(origRow, origCol, destRow, destCol)) { // if row/col difference is 2, make sure there is an intervening opponent piece
				validCoordinates = true;
				whiteCount--; // white lost a piece, it will remain red's turn
				updatePieceCounts();
			}
		} else if ((destRow < origRow) && (Math.abs(origRow - destRow) == 1) && (Math.abs(destCol - origCol) == 1)) {
			validCoordinates = true;
			currentColor.setText("white"); // no piece jumped, shift to other player
			updateTurnIndicator();
		} else if ((Math.abs(destRow - origRow) == 2) && (Math.abs(destCol - origCol) == 2) && interveningPiece(origRow, origCol, destRow, destCol)) {
			validCoordinates = true;
			whiteCount--; // white lost a piece, it will remain red's turn
			updatePieceCounts();
		}
		//return (validCoordinates && validPath);
		return validCoordinates;
	}
	
	/*
	 * Same as validMoveRed, but only applies when the current turn is for the white player
	 */
	
	public boolean validMoveWhite(int origRow, int origCol, int destRow, int destCol, boolean isJumping) {
		boolean validCoordinates = false;
		// boolean validPath = false;
		// forward coordinates for white means down and to left/right
		// for king piece, up/down and left/right
		if (isKing(imageGrid[origRow][origCol])) {
			if ((Math.abs(destRow - origRow) == 1) && (Math.abs(destCol - origCol) == 1)) {
				validCoordinates = true;
				currentColor.setText("red"); // no piece jumped, shift to other player
				updateTurnIndicator();
			} else if ((Math.abs(destRow - origRow) == 2) && (Math.abs(destCol - origCol) == 2) && interveningPiece(origRow, origCol, destRow, destCol)) {
				validCoordinates = true;
				redCount--; // red lost a piece, it will remain white's turn
				updatePieceCounts();
			}
		} else if ((destRow > origRow) && (Math.abs(destCol - origCol) == 1) && (Math.abs(destCol - origCol) == 1)) {
			validCoordinates = true;
			currentColor.setText("red"); // no piece jumped, shift to other player
			updateTurnIndicator();
		} else if ((Math.abs(destRow - origRow) == 2) && (Math.abs(destCol - origCol) == 2) && interveningPiece(origRow, origCol, destRow, destCol)) {
			validCoordinates = true;
			redCount--; // red lost a piece, it will remain white's turn
			updatePieceCounts();
		}
		
		// return (validCoordinates && validPath);
		return validCoordinates;
	}

	/*
	 * Take in a game piece's row/col, as well as the destination row/col it's attempting to move to.
	 * Determine the direction of the move, and return true if there is an intervening opponent piece along the "path".
	 * Only relevant for when a player is attempting to jump an opponent piece.
	 */

	public boolean interveningPiece(int origRow, int origCol, int destRow, int destCol) {
		boolean other_color = false;

		// Step 1: determine direction of the move
		int direction_int;
		if ((origRow > destRow) && (origCol < destCol)) {
			direction_int = 0; // up-right
		} else if ((origRow > destRow) && (origCol > destCol)) {
			direction_int = 1; // up-left
		} else if((origRow < destRow) && (origCol < destCol)) {
			direction_int = 2; // down-right
		} else direction_int = 3; // down-left
		
		// Now that we have the direction of the move, see if there is an intervening opponent piece along the path
		switch(direction_int) {
			case 0:
				// check space at origRow - 1, origCol + 1
				other_color = !colorMatches(imageGrid[origRow - 1][origCol + 1]);
				if (other_color) {
					jumpedRow = origRow - 1;
					jumpedCol = origCol + 1;
				}
			break;
			case 1:
				// check space at origRow - 1, origCol - 1
				other_color = !colorMatches(imageGrid[origRow - 1][origCol - 1]);
				if (other_color) {
					jumpedRow = origRow - 1;
					jumpedCol = origCol - 1;
				}
			break;
			case 2:
				// check space at origRow + 1, origCol + 1
				other_color = !colorMatches(imageGrid[origRow + 1][origCol + 1]);
				if (other_color) {
					jumpedRow = origRow + 1;
					jumpedCol = origCol + 1;
				}
			break;
			case 3:
				// check space at origRow + 1, origCol - 1
				other_color = !colorMatches(imageGrid[origRow + 1][origCol - 1]);
				if (other_color) {
					jumpedRow = origRow + 1;
					jumpedCol = origCol - 1;
				}
			break;
		}

		return other_color;
	}

	/*
	 * Update the Text object tracking the current player's turn
	 */
	public void updateTurnIndicator() {
		colorIndicator.setText("Current Turn: " + currentColor.getText());
		switch(currentColor.getText()) {
			case "white":
				colorIndicator.setFill(Color.ANTIQUEWHITE);
			break;
			case "red":
				colorIndicator.setFill(Color.RED);
			break;
		}
	}

	/*
	 * Update the Text objects displaying the current count of red/white pieces
	 */
	public void updatePieceCounts() {
		redTracker.setText("Red Count: " + redCount);
		whiteTracker.setText("White Count: " + whiteCount);
		if (redCount == 0 || whiteCount == 0) {
			game_over = true;
		}
	}

	private void checkForEnd() {
		if (!game_over) {
			return;
		}

		if (redCount == 0) {
			victoryAlert = new Alert(AlertType.INFORMATION, "White Player Wins!", new ButtonType("OK"));
		} else {
			victoryAlert = new Alert(AlertType.INFORMATION, "Red Player Wins!", new ButtonType("OK"));
		}
		victoryAlert.showAndWait();
		playAgainAlert = new Alert(AlertType.CONFIRMATION, "Would You Like to Play Again?", new ButtonType("Yes"), new ButtonType("No"));
		Optional<ButtonType> resp = playAgainAlert.showAndWait();playAgainAlert.showAndWait();

		if (resp.toString().equals("Optional[ButtonType [text=Yes, buttonData=OTHER]]")) {
			reset();
		} //if user clicked to enable the AI
	}

	private void reset() {
		redCount = 12;
		whiteCount = 12;
		jumpedRow = -1;
		jumpedCol = -1;
		game_over = false;
		selectedRow = -1;
		selectedCol = -1;
		setupOther();
		//Optional<ButtonType> r = aiQuery.showAndWait(); // return to this later
		//if (r.toString().equals("Optional[ButtonType [text=Yes, buttonData=OTHER]]")) {
	    //	   aiEnabled = true;
	    //} else aiEnabled = false;
		for (row = 0; row < 8; row++) {
			for (col = 0; col < 8; col++) {
				// need to re-initialize these on each iteration to avoid duplicate child nodes
				ImageView redTempView = new ImageView(redSpaceBlank);
				redTempView.setPreserveRatio(true);
				redTempView.setFitWidth(80.0);
				redTempView.setFitHeight(80.0);
				redTempView.setOnMouseClicked(event -> {
					// leave blank for now (only playing on black squares)
				});
				ImageView blackTempView = new ImageView(blackSpaceBlank);
				blackTempView.setPreserveRatio(true);
				blackTempView.setFitWidth(80.0);
				blackTempView.setFitHeight(80.0);
				blackTempView.setOnMouseClicked(event -> {
					imageEventHandler(blackTempView);
					checkForEnd();
				});
				
				// if-else to determine placement of red/black squares on the board
				if (row % 2 == 0) { // if filling in an "even" row (fill blacks on odd indices)
					switch(col % 2) {
					case 0:
						imageGrid[row][col] = redTempView;
						mainPane.add(redTempView, col, row);
						break;
					default:
						imageGrid[row][col] = blackTempView;
						mainPane.add(blackTempView, col, row);
					}
					initializeCoordinates(row, col);
				} else { // if filling in an "odd" row (fill blacks on even indices)
					switch(col % 2) {
					case 0:
						imageGrid[row][col] = blackTempView;
						mainPane.add(blackTempView, col, row);
						break;
					default:
						imageGrid[row][col] = redTempView;
						mainPane.add(redTempView, col, row);
					}
					initializeCoordinates(row, col);
				}
			} // inner for
		} // outer for
		root.getChildren().addAll(topMenu, mainPane, turnTracker);
		setupScene();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
