package com.davide.verbatiam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class MainActivity extends Activity {


    private ImageView startI;
    private ImageView exitI;
    private ImageView exitS;
    private ImageView shopI;
    private ImageView layoutcoin;
    private TextView textcoin;
    private TextView textScore;

    //Layout dello ship
    private ConstraintLayout shipL;
    private ImageView exitShipLayout;
    private ImageView green;
    private ImageView red;
    private ImageView ultimate;
    private ImageView backButton;
    private ImageView buyButtonRed;
    private ImageView buyButtonUltimate;
    private ImageView selectionGreen;
    private ImageView selectionRed;
    private ImageView selectionUltimate;

    //Layout dello shop
    private ConstraintLayout shopL;
    private ImageView shipSection;
    private ImageView gunSection;
    private ImageView upgradeSection;

    //Layout dei guns
    private ConstraintLayout gunL;
    private ImageView exitG;
    private ImageView gun1;
    private ImageView gun2;
    private ImageView gun3;
    private ImageView backButtonG;
    private ImageView buyButtonG1;
    private ImageView buyButtonG2;
    private ImageView buyButtonG3;

    //Layout degli upgrade
    private ConstraintLayout upgradeL;


    //Caricamento del gioco quando si preme su start
    private TextView caricamentoText;
    private ProgressBar caricamentoBar;

    //Handler usato per l'incemento della variabile "i" (fa partire il gioco)
    public Handler handlerCount = new Handler();
    public Runnable runnableCount;
    private boolean handlerFlag = true;
    private int i = 0;

    //Classe importata per passare dati di riferimento
    private DrawableCostants drawableCostants = new DrawableCostants();
    private DatabaseHelper db;
    private Storage storage = new Storage();
    private boolean dbFlag = false;

    private Costants costants = new Costants();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);
        costants.SCREEN_WIDTH = ds.widthPixels;
        costants.SCREEN_HEIGHT = ds.heightPixels;

        db = new DatabaseHelper(this);

        //3 tasti principali per interazione del menu
        startI = (ImageView) findViewById(R.id.start);
        exitI = (ImageView) findViewById(R.id.exit);
        shopI = (ImageView) findViewById(R.id.shop);
        layoutcoin = (ImageView) findViewById(R.id.layoutCoin);
        textcoin = (TextView) findViewById(R.id.textCoin);
        textScore = (TextView) findViewById(R.id.score);

        //Vari tipi di sezioni
        shipSection = (ImageView) findViewById(R.id.shipShop);
        gunSection = (ImageView) findViewById(R.id.gunShop);
        upgradeSection = (ImageView) findViewById(R.id.upgradeShop);

        //Layout collegato allo ship
        exitShipLayout = (ImageView) findViewById(R.id.shipExit);
        shipL = (ConstraintLayout) findViewById(R.id.shipLayout);
        green = (ImageView) findViewById(R.id.greenS);
        red = (ImageView) findViewById(R.id.redS);
        ultimate = (ImageView) findViewById(R.id.ultimateS);
        backButton = (ImageView) findViewById(R.id.backS);
        buyButtonRed = (ImageView) findViewById(R.id.buyR);
        buyButtonUltimate = (ImageView) findViewById(R.id.buyU);
        selectionGreen = (ImageView) findViewById(R.id.selectionG);
        selectionRed = (ImageView) findViewById(R.id.selectionR);
        selectionUltimate = (ImageView) findViewById(R.id.selectionU);

        //Layout collegato alle armi
        exitG = (ImageView) findViewById(R.id.gunExit);
        gunL = (ConstraintLayout) findViewById(R.id.gunLayout);
        gun1 = (ImageView) findViewById(R.id.gunS);
        gun2 = (ImageView) findViewById(R.id.gunM);
        gun3 = (ImageView) findViewById(R.id.gunH);
        backButtonG = (ImageView) findViewById(R.id.backG);
        buyButtonG1 = (ImageView) findViewById(R.id.buy1);
        buyButtonG2 = (ImageView) findViewById(R.id.buy2);
        buyButtonG3 = (ImageView) findViewById(R.id.buy3);

        //Layout collegato allo shop
        shopL = (ConstraintLayout) findViewById(R.id.shopLayout);
        exitS = (ImageView) findViewById(R.id.exitShop);

        //Caricamento del gioco quando si preme su start
        caricamentoBar = (ProgressBar) findViewById(R.id.caricamentoBar);
        caricamentoText = (TextView) findViewById(R.id.caricamentoText);

        Cursor res = db.selectData();
        if(res.getCount() != 0) {
            while (res.moveToNext()) {
                textcoin.setText((res.getString(1)));
                textScore.setText((res.getString(2)));
                storage.green = res.getInt(3);
                storage.red = res.getInt(4);
                storage.ultimate = res.getInt(5);
            }
        }

        if(storage.red == 0 && storage.ultimate == 0)
        {
            storage.green = 2;
            db.updateGreen(storage.green);
        }

        if(storage.green == 2)
        {
            drawableCostants.setPos(0);
        }
        if(storage.red == 2)
        {
            drawableCostants.setPos(1);
        }
        if(storage.ultimate == 2)
        {
            drawableCostants.setPos(2);
        }


        //Fa partire il gioco
        startI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caricamentoBar.setVisibility(View.VISIBLE);
                caricamentoText.setVisibility(View.VISIBLE);
                startI.setVisibility(View.INVISIBLE);
                exitI.setVisibility(View.INVISIBLE);
                shopI.setVisibility(View.INVISIBLE);
                handlerCount.post(runnableCount = new Runnable() {
                    @Override
                    public void run() {
                        if(handlerFlag == true)
                        {
                            System.out.println(caricamentoBar.getProgress());
                            if(caricamentoBar.getProgress() == 100 && count() >= 100)
                            {
                                startGame();
                                startI.setOnClickListener(null);
                                handlerCount.removeCallbacks(this);
                                handlerFlag = false;
                                i = 0;
                                caricamentoBar.setProgress(i);
                            }
                            else if(caricamentoBar.getProgress() < 100 && count() < 100)
                            {
                                count();
                                caricamentoBar.setProgress(count());
                                handlerFlag = true;
                            }
                            handlerCount.postDelayed(this, 500);
                        }

                    }
                });
            }
        });

        //Esce dall'applicazione
        exitI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        //Apre la sezione shop
        shopI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startI.setVisibility(View.INVISIBLE);
                exitI.setVisibility(View.INVISIBLE);
                shopI.setVisibility(View.INVISIBLE);
                shopL.setVisibility(View.VISIBLE);
            }
        });

        //Esce dalla sezione shop
        exitS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.INVISIBLE);
                startI.setVisibility(View.VISIBLE);
                exitI.setVisibility(View.VISIBLE);
                shopI.setVisibility(View.VISIBLE);
            }
        });

        //Premendo sull'immagine selezione astonavi,
        //si apre un secondo layout (Ship Layout)
        shipSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.INVISIBLE);
                shipL.setVisibility(View.VISIBLE);
            }
        });

        //Esce dal ship layout con il pulsante exit
        //rendendo visibile lo shop layout e
        //invisibile lo ship layout
        exitShipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.VISIBLE);
                shipL.setVisibility(View.INVISIBLE);
            }
        });

        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.VISIBLE);
                red.setVisibility(View.INVISIBLE);
                ultimate.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);
                selectionGreen.setVisibility(View.VISIBLE);
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.INVISIBLE);
                red.setVisibility(View.VISIBLE);
                ultimate.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);

                if(storage.red >= 1)
                {
                    buyButtonRed.setVisibility(View.INVISIBLE);
                    selectionRed.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonRed.setVisibility(View.VISIBLE);
                    selectionRed.setVisibility(View.INVISIBLE);
                }

                //Va alla posizione della astronave verde
                red.setX(336);
                red.setY(300);
            }
        });

        ultimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.INVISIBLE);
                red.setVisibility(View.INVISIBLE);
                ultimate.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);

                if(storage.ultimate >= 1)
                {
                    buyButtonUltimate.setVisibility(View.INVISIBLE);
                    selectionUltimate.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonUltimate.setVisibility(View.VISIBLE);
                    selectionUltimate.setVisibility(View.INVISIBLE);
                }


                //Va alla posizione della astronave verde
                ultimate.setX(336);
                ultimate.setY(290);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green.setVisibility(View.VISIBLE);
                red.setVisibility(View.VISIBLE);
                ultimate.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.INVISIBLE);
                selectionGreen.setVisibility(View.INVISIBLE);
                buyButtonUltimate.setVisibility(View.INVISIBLE);
                selectionUltimate.setVisibility(View.INVISIBLE);
                buyButtonRed.setVisibility(View.INVISIBLE);
                selectionRed.setVisibility(View.INVISIBLE);

                //Ritorna alla posizione originale
                green.setX(336);
                green.setY(306);

                //Ritorna alla posizione originale
                red.setX(339);
                red.setY(685);

                //Ritorna alla posizione originale
                ultimate.setX(339);
                ultimate.setY(1083);
            }
        });

        selectionGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaGreen();
            }
        });

        buyButtonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyRed();

                if(storage.red >= 1)
                {
                    buyButtonRed.setVisibility(View.INVISIBLE);
                    selectionRed.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonRed.setVisibility(View.VISIBLE);
                    selectionRed.setVisibility(View.INVISIBLE);
                }
            }
        });

        selectionRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaRed();
            }
        });

        buyButtonUltimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyUltimate();

                if(storage.ultimate >= 1)
                {
                    buyButtonUltimate.setVisibility(View.INVISIBLE);
                    selectionUltimate.setVisibility(View.VISIBLE);
                }
                else
                {
                    buyButtonUltimate.setVisibility(View.VISIBLE);
                    selectionUltimate.setVisibility(View.INVISIBLE);
                }
            }
        });

        selectionUltimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaUltimate();
            }
        });

        //Premendo sull'immagine selezione astonavi,
        //si apre un secondo layout (gun Layout)
        gunSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.INVISIBLE);
                gunL.setVisibility(View.VISIBLE);
            }
        });

        //Esce dal ship layout con il pulsante exit
        //rendendo visibile lo shop layout e
        //invisibile gun layout
        exitG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopL.setVisibility(View.VISIBLE);
                gunL.setVisibility(View.INVISIBLE);
            }
        });

        gun1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gun1.setVisibility(View.VISIBLE);
                gun2.setVisibility(View.INVISIBLE);
                gun3.setVisibility(View.INVISIBLE);
                backButtonG.setVisibility(View.VISIBLE);
                buyButtonG1.setVisibility(View.VISIBLE);
            }
        });

        gun2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gun1.setVisibility(View.INVISIBLE);
                gun2.setVisibility(View.VISIBLE);
                gun3.setVisibility(View.INVISIBLE);
                backButtonG.setVisibility(View.VISIBLE);
                buyButtonG2.setVisibility(View.VISIBLE);

                //Va alla posizione della astronave verde
                gun2.setX(336);
                gun2.setY(300);
            }
        });

        gun3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gun1.setVisibility(View.INVISIBLE);
                gun2.setVisibility(View.INVISIBLE);
                gun3.setVisibility(View.VISIBLE);
                backButtonG.setVisibility(View.VISIBLE);
                buyButtonG3.setVisibility(View.VISIBLE);

                //Va alla posizione della astronave verde
                gun3.setX(336);
                gun3.setY(306);

            }
        });

        backButtonG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gun1.setVisibility(View.VISIBLE);
                gun2.setVisibility(View.VISIBLE);
                gun3.setVisibility(View.VISIBLE);
                backButtonG.setVisibility(View.INVISIBLE);
                buyButtonG1.setVisibility(View.INVISIBLE);
                buyButtonG3.setVisibility(View.INVISIBLE);
                buyButtonG2.setVisibility(View.INVISIBLE);

                //Ritorna alla posizione originale
                gun1.setX(336);
                gun1.setY(306);

                //Ritorna alla posizione originale
                gun2.setX(339);
                gun2.setY(685);

                //Ritorna alla posizione originale
                gun3.setX(336);
                gun3.setY(1155);
            }
        });


    }

    public void buyRed()
    {
        long redCoin = 5000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= redCoin && storage.red == 0)
        {
            currentCoin = currentCoin - redCoin;
            storage.coinStorageF = storage.coinStorageF - redCoin;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.red = 1;
            db.updateRed(storage.red);

        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa nave, perchè non hai abbastanza soldi. \n L'astronave costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa astronave");
        }
    }

    public void buyUltimate()
    {
        long ultimateCoin = 20000L;
        long currentCoin = 0L;

        Cursor res = db.selectData();

        while (res.moveToNext()) {
            currentCoin = res.getLong(1);
        }

        if(currentCoin >= ultimateCoin && storage.ultimate == 0)
        {
            currentCoin = currentCoin - ultimateCoin;
            storage.coinStorageF = storage.coinStorageF - ultimateCoin;

            db.updateData(currentCoin);
            textcoin.setText(currentCoin+"");

            storage.ultimate = 1;
            db.updateUltimate(storage.ultimate);

        }
        else
        {
            System.out.println("Mi dispiace ma non puoi acquistare questa nave, perchè non hai abbastanza soldi. \n L'astronave costa 5000 coin mentre tu ne possiedi " + currentCoin + "\nOppure e' perche' hai gia' acquistato questa astronave");
        }
    }

    public void selezionaGreen()
    {
        if(storage.green == 1)
        {
            storage.green = 2;

            if(storage.red == 2 || storage.ultimate == 2)
            {
                storage.red = 1;
                db.updateRed(storage.red);

                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                Cursor res = db.selectData();
                while (res.moveToNext())
                {
                    System.out.println(res.getInt(3)+"");
                    System.out.println(res.getInt(4)+"");
                    System.out.println(res.getInt(5)+"");
                }
            }

            db.updateGreen(storage.green);
            drawableCostants.setPos(0);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaRed()
    {
        if(storage.red == 1)
        {
            storage.red = 2;

            if(storage.green == 2 || storage.ultimate == 2)
            {
                storage.green = 1;
                db.updateGreen(storage.green);

                storage.ultimate = 1;
                db.updateUltimate(storage.ultimate);

                Cursor res = db.selectData();
                while (res.moveToNext())
                {
                    System.out.println(res.getInt(3)+"");
                    System.out.println(res.getInt(4)+"");
                    System.out.println(res.getInt(5)+"");
                }
            }

            db.updateRed(storage.red);
            drawableCostants.setPos(1);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    public void selezionaUltimate()
    {
        if(storage.ultimate == 1)
        {
            storage.ultimate = 2;

            if(storage.green == 2 || storage.red == 2)
            {
                storage.green = 1;
                db.updateGreen(storage.green);

                storage.red = 1;
                db.updateRed(storage.red);

                Cursor res = db.selectData();
                while (res.moveToNext())
                {
                    System.out.println(res.getInt(3)+"");
                    System.out.println(res.getInt(4)+"");
                    System.out.println(res.getInt(5)+"");
                }
            }

            db.updateUltimate(storage.ultimate);
            drawableCostants.setPos(2);
        }
        else
        {
            System.out.println("Messaggio di errore");
        }
    }

    //Metodo usato per caricare la seconda activity
    public void startGame()
    {
        Cursor res = db.selectData();
        while (res.moveToNext()) {
            storage.coinStorageF = res.getLong(1);
        }
        Intent intent = new Intent(MainActivity.this, GamePanel.class);
        startActivity(intent);
    }

    //Metodo usato per incrementare la progress bar
    public int count()
    {
        if(i < 50)
        {
            i = i + 5;
        }
        else if(i >=50)
        {
            i = i + 2;
        }
        return i;
    }

}

