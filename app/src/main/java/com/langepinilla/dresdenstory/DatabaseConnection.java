package com.langepinilla.dresdenstory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//This class is modelled after general Android architecture advice, see for example https://developer.android.com/codelabs/android-training-livedata-viewmodel#0 for an introduction.

//This class is where we actually create the RoomDatabase that handles our Chapter entities with the ChapterDAO.

//here we define the Chapter.class as the source after which to model the entities and therefore the structure of the SQLite database. As the database is created newly on first run, it will be version 1.
@Database(entities = {Chapter.class}, version = 1)
public abstract class DatabaseConnection extends RoomDatabase {

    //connect the RoomDatabase to the ChapterDAO
    public abstract ChapterDAO getChapterDAO();

    //initialize variables, also define the databaseWriteExecutor that will be used to handle access to the database
    private static DatabaseConnection INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //this method is used to initialize the database in the repository, it also calls the sRoomDatabaseCallback
    static DatabaseConnection getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseConnection.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseConnection.class, "db.db")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //in this callback we create our chapters when the database is newly created on the first app run (and delete all pre-existing chapters just in case)
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                ChapterDAO dao = INSTANCE.getChapterDAO();
                dao.deleteAll();

                Chapter chapter = new Chapter(0, "Introduction", "Middle of Altmarkt", 51.049500, 13.738000, "A short introduction to the story.", "<p>Welcome to this interactive story experience! In this story, you follow the path of an older Dresden resident and her grandson while discovering the city for yourself.</p> <p>You will be provided with texts like this and embedded images like this one of the flag of Dresden:</p> <img src=\"chapter0_image1\" alt=\"Flag of Dresden\"> <p>At the end of each chapter you will need to answer a small question to unlock the next chapter. If you can't solve it, you will get a hint to help you.</p>", "To continue, type 'understood' as an answer. You will never need to write full sentences, just single words or names.", "Make sure you didn't mistype the answer.", "understood,verstanden,okay", true);
                dao.insert(chapter);
                chapter = new Chapter(1, "Dresden in the 50s", "In front of Haus Altmarkt", 51.049917, 13.738918, "The first chapter introduces our two protagonists and introduces you to Dresden after the Second World War.", "<p>On a stroll through Dresden Altstadt Helga tells her grandson Lukas about her life in Dresden. She was born in 1940 and lived in Dresden her whole life, so she has lots of stories to share with Lukas, who was born in 2000 and is currently visiting her for a week. They just started their walk near the Altmarkt as Helga has a flat nearby and discuss her youth in Dresden.</p> <p>Lukas: Wow, grandma, so you're really saying this whole area was in ruins after the war?</p> <p>Helga: Yes, exactly, most of the inner city was destroyed during the bombings. The buildings you see here were among the first to be rebuilt in the 1950s, for example Haus Altmarkt:</p> <img src='chapter1_image1' alt='Haus Altmarkt'> <p>Lukas: Oh, interesting, so this was also built in the German Democratic Republic (GDR)? Because it doesn't look like the architecture I would have expected!</p> <p>Helga: Yes, in the early years after the war, the GDR also built houses in a historicist style. Also the decorations of this house show what was important to people after the war: To relax and have a good time as is symbolized with drinking and making music here.</p> <img src='chapter1_image2' alt='Altmarkt sculptures'> <p>Lukas: Haha, nice, I didn't notice that before!</p> <p>Helga: On the other side of the square you can also see the Kulturpalast, which is the more famous kind of GDR architecture, but let's walk for a bit before we talk about it more.</p>", "What was one of the earliest buildings to be built after World War Two called again?", "You can check the story text or look at the writing between the statues in the image.", "hausaltmarkt,housealtmarkt", false);
                dao.insert(chapter);
                chapter = new Chapter(2, "Dresden in the GDR", "West side of Kulturpalast", 51.051205, 13.737405, "In this chapter you learn more about Dresden in the time of the GDR (1949-1990).", "<p>Helga: Alright, now we are on the side of Kulturpalast, and here you can see a big mural, called 'The way of the red flag'.</p> <p>Lukas: And what is so remarkable about this? Don't we have murals all over town?</p> <p>Helga: Well, I think this building emphasizes some aspects of the GDR quite well. The Kulturpalast was quite unusual in its time because it wasn't built as a high-rise building in Stalinist style as originally intended but follows the International Style. It still has some socialist ideas integrated, for example the literally and figuratively transparent facade. And this influence of GDR doctrine is even more clear in this mural showing important moments and figures of GDR history.</p> <img src='chapter2_image1' alt='The Way of The Red Flag'> <p>Lukas: Okay, I see now why you wanted to show me this! How was growing up in the GDR anyways?</p> <p>Helga: Like with this building, there were two sides to the same coin. On one hand, there were very progressive and plausible concepts. But on the other hand, they were often driven by necessity and the totalitarian government didn't allow for alternative opinions to what was dictated by them.</p> <p>Lukas: That sounds harsh, grandma! How do you feel about the reunification then?</p> <p>Helga: That is a two-sided thing as well, I'd say. Come on, I think I can better talk about this at our next stop.</p>", "What is the main signal color used in the mural?", "It is one of the colors in the German flag and also generally associated with the GDR and the historic Eastern Bloc.", "red,rot", false);
                dao.insert(chapter);
                chapter = new Chapter(3, "Dresden after the reunification", "Neumarkt", 51.051451, 13.740513, "This chapter is about the rebuilding of the city center after the German reunification.", "<p>This chapter is just a placeholder to better showcase the app for now.</p>", "Will you recommend this app to your friends?", "Try a simple, one-word answer.", "yes,no,maybe,ja,nein,vielleicht", false);
                dao.insert(chapter);
                chapter = new Chapter(4, "Remnants of old Dresden", "In front of the Fürstenzug", 51.052804, 13.739046, "In this chapter you learn about what buildings are actually older than 1950 and are still standing nowadays.", "<p>This chapter is just a placeholder to better showcase the app for now.</p>", "Did you enjoy answering these questions?", "Try a simple, one-word answer.", "yes,no,maybe,ja,nein,vielleicht", false);
                dao.insert(chapter);
            });
        }
    };
}