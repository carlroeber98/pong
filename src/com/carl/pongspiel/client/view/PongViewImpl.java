package com.carl.pongspiel.client.view;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Div;

import com.carl.pongspiel.client.presenter.PongPresenter;
import com.carl.pongspiel.client.ui.GamePreferences;
import com.carl.pongspiel.client.ui.Position;
import com.carl.pongspiel.shared.Languages;
import com.carl.pongspiel.shared.model.PlayerType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PongViewImpl extends Composite implements PongView {

	@UiTemplate("PongView.ui.xml")
	public interface PongViewUiBinder extends UiBinder<Widget, PongViewImpl> {

		Widget createAndBindUi(PongViewImpl viewImpl);
	}

	private static PongViewUiBinder uiBinder = GWT.create(PongViewUiBinder.class);
	
	private PongView.Presenter presenter;
	
	@Override
	public void setPresenter(PongPresenter presenter) {
		this.presenter = presenter;
	}
	
	public PongViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		Window.setTitle("Pong");
		initClickable();
		Window.enableScrolling(false);
	}
	
	/**
	 * Variables
	 */
	private int xDirection;
	private int yDirection;
	private int batSpeed;
	private int ballSpeed;
	private int gameFieldBorder;
	private Position ballPosition;
	private Position batPlayer1Position;
	private Position batPlayer2Position;
	private Integer gameFieldWidth;
	private Integer gameFieldHeight;
	private Timer timer;

	/**
	 * Ui-Fields
	 */
	@UiField
	Button startButton;
	
	@UiField
	Button breakButton;

	@UiField
	Div gameField;

	@UiField
	Widget game;

	@UiField
	Label pointsLabel;

	@UiField
	Widget ball;

	@UiField
	Widget batBot;
	
	@UiField
	Widget batPlayer;
	
	@UiField
	Button logoutButton;
	
	/**
	 * Ui-Handler
	 */
	@UiHandler("startButton")
	public void onstartButtonClicked(ClickEvent e) {
		presenter.startGame();
	}
	
	@UiHandler("logoutButton")
	public void onlogoutButtonClicked(ClickEvent e) {
		timerCancel();
		presenter.logout();
	}
	
	@UiHandler("breakButton")
	public void onBreakButtonClicked(ClickEvent e) {
		if(timer != null && timer.isRunning()){
			timer.cancel();
			breakButton.setText(Languages.continue2());
		}else{
			timer.scheduleRepeating(ballSpeed);
			breakButton.setText(Languages.pause());
		}
	}
	
	public void addKeyHandlers() {
		RootPanel.get().addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isUpArrow()) {
					if (batPlayer.getElement().getOffsetTop() - batSpeed >  0) {
						batPlayer.getElement().getStyle().setTop(batPlayer.getElement().getOffsetTop() - batSpeed, Unit.PX);
					}
					if (batPlayer.getElement().getOffsetTop() - batSpeed < 0 || batPlayer.getElement().getOffsetTop() - batSpeed == 0) {
						batPlayer.getElement().getStyle().setTop(0, Unit.PX);
					}
				}
				if (event.isDownArrow()) {
					if (batPlayer.getElement().getOffsetTop() + batSpeed < gameFieldHeight) {
						batPlayer.getElement().getStyle().setTop(batPlayer.getElement().getOffsetTop() + batSpeed, Unit.PX);
					}
					if (batPlayer.getElement().getOffsetTop() + batSpeed > gameFieldHeight - batPlayer.getOffsetHeight() + gameFieldBorder
							|| batPlayer.getElement().getOffsetTop() + batSpeed == gameFieldHeight - batPlayer.getOffsetHeight() + gameFieldBorder) {
						batPlayer.getElement().getStyle().setTop(gameFieldHeight - batPlayer.getOffsetHeight(), Unit.PX);
					}
				}
			}
		}, KeyDownEvent.getType());
	}

	
	public void buildGameField(GamePreferences gamePreferences) {
		pointsLabel.setColor(gamePreferences.getLabelPointsColor().getColor());
		pointsLabel.getElement().getStyle().setFontSize(gamePreferences.getLabelPointsSize(), Unit.PX);
		startButton.getElement().getStyle().setBackgroundColor(gamePreferences.getButtonBackgroundColor().getColor());
		startButton.setColor(gamePreferences.getButtonFontColor().getColor());
		startButton.getElement().getStyle().setFontSize(gamePreferences.getButtonSize(), Unit.PX);
		logoutButton.getElement().getStyle().setBackgroundColor(gamePreferences.getButtonBackgroundColor().getColor());
		logoutButton.setColor(gamePreferences.getButtonFontColor().getColor());
		logoutButton.getElement().getStyle().setFontSize(gamePreferences.getButtonSize(), Unit.PX);
		breakButton.setColor(gamePreferences.getButtonFontColor().getColor());
		breakButton.getElement().getStyle().setBackgroundColor(gamePreferences.getButtonBackgroundColor().getColor());
		breakButton.getElement().getStyle().setFontSize(gamePreferences.getButtonSize(), Unit.PX);
		
		gameFieldWidth = gamePreferences.getGameFieldWidth();
		gameFieldHeight = gamePreferences.getGameFieldHeight();
		gameFieldBorder = gamePreferences.getGameFieldBorder();
		gameField.getElement().getStyle().setHeight(gameFieldHeight, Unit.PX);
		gameField.getElement().getStyle().setWidth(gameFieldWidth, Unit.PX);
		gameField.getElement().getStyle().setBorderWidth(gameFieldBorder, Unit.PX);
		
		gameField.getElement().getStyle().setBackgroundColor(gamePreferences.getGameFieldBackgroundColor().getColor());
		gameField.getElement().getStyle().setBorderColor(gamePreferences.getGameFieldBorderColor().getColor());
		
		ball.getElement().getStyle().setWidth(gamePreferences.getBallWidth(), Unit.PX);
		ball.getElement().getStyle().setHeight(gamePreferences.getBallHeight(), Unit.PX);
		ball.getElement().getStyle().setBackgroundColor(gamePreferences.getBallColor().getColor());
		
		batPlayer.getElement().getStyle().setWidth(gamePreferences.getBatPlayer1Width(), Unit.PX);
		batPlayer.getElement().getStyle().setHeight(gamePreferences.getBatPlayer1Height(), Unit.PX);
		batPlayer.getElement().getStyle().setBackgroundColor(gamePreferences.getBatPlayer1Color().getColor());
		
		batBot.getElement().getStyle().setWidth(gamePreferences.getBatPlayer2Width(), Unit.PX);
		batBot.getElement().getStyle().setHeight(gamePreferences.getBatPlayer2Height(), Unit.PX);
		batBot.getElement().getStyle().setBackgroundColor(gamePreferences.getBatPlayer2Color().getColor());
		
		initGameFieldPositions(gamePreferences.getBatPlayer1PositionLeft(), gamePreferences.getBatPlayer2PositionRight());
		
		resetGameElements();
		
		game.setVisible(true);
	}
	
	public void rebuildGameField(GamePreferences gamePreferences){
		pointsLabel.getElement().getStyle().setFontSize(gamePreferences.getLabelPointsSize(), Unit.PX);
		breakButton.getElement().getStyle().setFontSize(gamePreferences.getButtonSize(), Unit.PX);
		logoutButton.getElement().getStyle().setFontSize(gamePreferences.getButtonSize(), Unit.PX);
		startButton.getElement().getStyle().setFontSize(gamePreferences.getButtonSize(), Unit.PX);
		
		gameFieldWidth = gamePreferences.getGameFieldWidth();
		gameFieldHeight = gamePreferences.getGameFieldHeight();
		gameFieldBorder = gamePreferences.getGameFieldBorder();
		gameField.getElement().getStyle().setHeight(gameFieldHeight, Unit.PX);
		gameField.getElement().getStyle().setWidth(gameFieldWidth, Unit.PX);
		gameField.getElement().getStyle().setBorderWidth(gameFieldBorder, Unit.PX);
		
		ball.getElement().getStyle().setWidth(gamePreferences.getBallWidth(), Unit.PX);
		ball.getElement().getStyle().setHeight(gamePreferences.getBallHeight(), Unit.PX);
		
		batPlayer.getElement().getStyle().setWidth(gamePreferences.getBatPlayer1Width(), Unit.PX);
		batPlayer.getElement().getStyle().setHeight(gamePreferences.getBatPlayer1Height(), Unit.PX);
		
		batBot.getElement().getStyle().setWidth(gamePreferences.getBatPlayer2Width(), Unit.PX);
		batBot.getElement().getStyle().setHeight(gamePreferences.getBatPlayer2Height(), Unit.PX);
		
		initGameFieldPositions(gamePreferences.getBatPlayer1PositionLeft(), gamePreferences.getBatPlayer2PositionRight());
		
		resetGameElements();
	}
	
	public void initGameFieldPositions(int batPlayer1PositionLeft, int batPlayer2PositionRight) {
		ballPosition = new Position(gameFieldHeight / 2 - ball.getOffsetHeight() / 2, gameFieldWidth / 2 - ball.getOffsetWidth() / 2);
		batPlayer1Position = new Position(gameFieldHeight / 2 - batPlayer.getOffsetHeight() / 2, batPlayer1PositionLeft);
		batPlayer2Position = new Position(gameFieldHeight / 2 - batBot.getOffsetHeight() / 2, gameFieldWidth - batPlayer2PositionRight - gameFieldBorder);
	}
	
	public void resetGameElements() {
		ball.getElement().getStyle().setLeft(ballPosition.getLeft(), Unit.PX);
		ball.getElement().getStyle().setTop(ballPosition.getTop(), Unit.PX);

		batPlayer.getElement().getStyle().setLeft(batPlayer1Position.getLeft(), Unit.PX);
		batPlayer.getElement().getStyle().setTop(batPlayer1Position.getTop(), Unit.PX);

		batBot.getElement().getStyle().setLeft(batPlayer2Position.getLeft(), Unit.PX);
		batBot.getElement().getStyle().setTop(batPlayer2Position.getTop(), Unit.PX);
	}
	
	public void moveBall(final int xDir, final int yDir, final int ballSp) {
		xDirection = xDir;
		yDirection = yDir;
		ballSpeed = ballSp;
		timer = new Timer(){
			@Override
			public void run() {
				
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//				MOVEMENT_DEBUG																															//
//				GWT.log("(ball)[1] " + (ball.getElement().getOffsetLeft() + ball.getOffsetWidth()) + " [2] " + batBot.getElement().getOffsetLeft() + 	//
//				" [3] " + (ball.getElement().getOffsetTop() + ball.getOffsetHeight()) + " [4] " + batBot.getElement().getOffsetTop() + 					//
//				" [5] " + ball.getElement().getOffsetTop() + " [6] " + (batBot.getElement().getOffsetTop() + batBot.getOffsetHeight()));				//
//				GWT.log("(batBot)[Left] " + batBot.getElement().getOffsetLeft() + ", " + "[Top] " + batBot.getElement().getOffsetTop() +  ", " + 		//
//				"[Bottom] " + (batPlayer.getElement().getOffsetTop() + batBot.getOffsetHeight()));														//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if (ball.getElement().getOffsetTop() <= 0 || ball.getElement().getOffsetTop() + ball.getOffsetHeight() >= gameFieldHeight ) {
				 	yDirection = presenter.reboundTopBottom(yDirection);
				}
				if (ball.getElement().getOffsetLeft() <= 0) {
						presenter.pointsPlusOne(PlayerType.BOT);
					cancel();
				}
				if (ball.getElement().getOffsetLeft() >= gameFieldWidth - gameFieldBorder) {
					presenter.pointsPlusOne(PlayerType.PLAYER);
					cancel();
				}
				if (ball.getElement().getOffsetLeft() == batPlayer.getElement().getOffsetLeft() + batPlayer.getOffsetWidth() && 
						ball.getElement().getOffsetTop() + ball.getOffsetHeight() >= batPlayer.getElement().getOffsetTop() &&  
						ball.getElement().getOffsetTop() <= batPlayer.getElement().getOffsetTop() + batPlayer.getOffsetHeight()){
					presenter.reboundBat(xDirection);
				}
				if (ball.getElement().getOffsetLeft() + ball.getOffsetWidth() == batBot.getElement().getOffsetLeft() && 
						ball.getElement().getOffsetTop() + ball.getOffsetHeight() >= batBot.getElement().getOffsetTop() &&  
						ball.getElement().getOffsetTop() <= batBot.getElement().getOffsetTop() + batBot.getOffsetHeight()){
					presenter.reboundBat(xDirection);
				}
				
//				if (ball.getElement().getOffsetTop() + ball.getOffsetHeight() == batPlayer.getElement().getOffsetTop() &&
//						ball.getElement().getOffsetLeft() > batPlayer.getElement().getOffsetLeft() &&
//						ball.getElement().getOffsetLeft() + ball.getOffsetWidth() < batPlayer.getElement().getOffsetLeft() + batPlayer.getOffsetWidth()){
//					presenter.reboundBat(xDirection);
//				}
//				if (ball.getElement().getOffsetTop() == batPlayer.getElement().getOffsetTop() + batPlayer.getOffsetHeight() &&
//						ball.getElement().getOffsetLeft() > batPlayer.getElement().getOffsetLeft() &&
//						ball.getElement().getOffsetLeft() + ball.getOffsetWidth() < batPlayer.getElement().getOffsetLeft() + batPlayer.getOffsetWidth()){
//					presenter.reboundBat(xDirection);
//				}
				//Funktion für die Kanten des schlägers
				
				moveBatBot();
				
				ball.getElement().getStyle().setTop(ball.getElement().getOffsetTop() + yDirection, Unit.PX);
				ball.getElement().getStyle().setLeft(ball.getElement().getOffsetLeft() + xDirection, Unit.PX);
			}
		};
		timer.scheduleRepeating(ballSpeed);
	}
	
	public void moveBatBot(){
		if (ball.getElement().getOffsetTop() + 0.5 > batBot.getOffsetHeight() / 2 && ball.getElement().getOffsetTop() + ball.getElement().getOffsetHeight() - 2.5 < gameFieldHeight + gameFieldBorder - batBot.getOffsetHeight() / 2){
			batBot.getElement().getStyle().setTop(ball.getElement().getOffsetTop() - batBot.getOffsetHeight() / 2, Unit.PX);
			//wo kommmt die 2 her???
		}
	}
	
	public void initClickable(){
		startButton.getElement().addClassName("clickable");
		logoutButton.getElement().addClassName("clickable");
		breakButton.getElement().addClassName("clickable");
	}
	
	public void setXyDirection(int xDir, int yDir){
		this.xDirection = xDir;
		this.yDirection = yDir;
	}

	public void setbatSpeed(int speed){
		this.batSpeed = speed;
	}

	public void setStartButtonVisible(boolean visible) {
		startButton.setVisible(visible);
	}

	public void setPoints(int player, int bot) {
		pointsLabel.setText(player + " : " + bot);
	}

	public void setBreakButtonVisible(boolean visible) {
		breakButton.setVisible(visible);
	}
	
	public void timerCancel(){
		if (timer != null && timer.isRunning())
			timer.cancel();
	}
	
	public void setBreakButton(String text){
		breakButton.setText(text);
	}

}
