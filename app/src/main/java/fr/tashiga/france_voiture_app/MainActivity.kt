package fr.tashiga.france_voiture_app

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.sql.*
import java.util.ArrayList
import androidx.appcompat.widget.Toolbar



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadAccueil()
    }

    fun loadAccueil() {
        setContentView(R.layout.activity_main)
        var boutonArticle = findViewById<Button>(R.id.btn_article) as Button
        var boutonPageConnexion = findViewById<Button>(R.id.connexion) as Button
        var citroen = findViewById<ImageView>(R.id.imageCitroen) as ImageView
        var peugeot = findViewById<ImageView>(R.id.imagePeugeot) as ImageView
        var renault = findViewById<ImageView>(R.id.imageRenault) as ImageView

        boutonArticle.setOnClickListener {
            loadBoutique("")
        }
        citroen.setOnClickListener {
            loadBoutique("Citroen")
        }
        peugeot.setOnClickListener {
            loadBoutique("Peugeot")
        }
        renault.setOnClickListener {
            loadBoutique("Renault")
        }
        boutonPageConnexion.setOnClickListener {
            loadConnexion()
        }

    }


    fun loadConnexion() {
        setContentView(R.layout.connexion)
        var retour = findViewById<TextView>(R.id.textView_retour) as TextView
        retour.setOnClickListener {
            loadAccueil()
        }
    }


    fun getListOfArticles(query: String) : ArrayList<Article>?{
        MySQL.MySQL("user", "mdp")
        var connection:Connection? = MySQL.connectMySQL()
        val resultSet:ResultSet? = MySQL.executeRequete(connection!!, query)
        var article: Article
        val items = ArrayList<Article>()
        while (resultSet!!.next()) {
            var nomVendeur = resultSet.getString("nom")+" "+resultSet.getString("prenom")
            var image: Int
            if(resultSet.getString("categorie").equals("moteur")) {
                image = R.drawable.moteur
            }
            else if(resultSet.getString("categorie").equals("pneus")) {
                image = R.drawable.pneu
            }
            else {
                image = R.drawable.autre
            }
            var description:String
            if(resultSet.getString("description").isNullOrEmpty()) {
                description = ""
            }
            else {
                description = resultSet.getString("description")
            }

            article = Article( resultSet.getString("article"),
                    description,
                    image,
                    resultSet.getString("prix"),
                    nomVendeur,
                    resultSet.getString("categorie")
            )
            items.add(article)
        }
        return items

    }

    fun loadBoutique(marque:String) {

        setContentView(R.layout.boutique)

        var recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewBoutique) as RecyclerView
        var retour = findViewById<TextView>(R.id.retour) as TextView
        var toolbar: Toolbar = findViewById(R.id.boutique_toolbar) as Toolbar
        var imageSearch = toolbar.findViewById<ImageView>(R.id.ImageView_Search) as ImageView
        var editText = toolbar.findViewById<EditText>(R.id.toolbar_searchText) as EditText
        var articleAdapter : ArticleAdapter


        setSupportActionBar(toolbar)
        var query:String
        if(marque.isNullOrEmpty()) {
            query = "select distinct article.nom as article, article.description, article.prix, article.categorie, vendeur.nom, vendeur.prenom from article inner join ajouter on article.idArticle = ajouter.idArticle inner join vendeur on ajouter.idVendeur = vendeur.idVendeur inner join peut_convenir_avec on article.idArticle = peut_convenir_avec.idArticle inner join voiture on peut_convenir_avec.idVoiture = voiture.idVoiture"
        }
        else {
            query = "select distinct article.nom as article, article.description, article.prix, article.categorie, vendeur.nom, vendeur.prenom, voiture.marque from article inner join ajouter on article.idArticle = ajouter.idArticle inner join vendeur on ajouter.idVendeur = vendeur.idVendeur inner join peut_convenir_avec on article.idArticle = peut_convenir_avec.idArticle inner join voiture on peut_convenir_avec.idVoiture = voiture.idVoiture where marque='" + marque + "'"        }

        //recuperer la list des articles depuis la BD
        val items = getListOfArticles(query)
        articleAdapter = ArticleAdapter(items!!)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = articleAdapter
        }

        imageSearch.setOnClickListener {
            val search = editText.text.toString()
            articleAdapter.filter.filter(search)
        }

        retour.setOnClickListener {
            loadAccueil()
        }

    }

}