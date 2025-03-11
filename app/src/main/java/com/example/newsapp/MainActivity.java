package com.example.newsapp;


import static android.net.http.SslCertificate.restoreState;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.newsapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<Source> sources;
    private ActivityMainBinding binding;
    private ArrayList<Source> filteredList;

    private Map<String, String> countryCodeToName;
    private Map<String, String> languageCodeToName = new HashMap<>();


    private NewsArticleAdapter articleAdapter;
    private final ArrayList<Article> articleList = new ArrayList<>();

    //store the color of the category
    private final Map<String, Integer> categoryColorMap = new HashMap<>();

    //current filter selections
    private String selectedTopic = null;
    private String selectedCountry = null;
    private String selectedLanguage = null;

    //keys for saving instance state
    private static final String STATE_SELECTED_TOPIC = "selected_topic";
    private static final String STATE_SELECTED_COUNTRY = "selected_country";
    private static final String STATE_SELECTED_LANGUAGE = "selected_language";
    private static final String STATE_SOURCES = "sources";
    private static final String STATE_FILTERED_LIST = "filtered_list";
    private static final String STATE_ARTICLES = "articles";
    private static final String STATE_CURRENT_ARTICLE = "current_article";
    private static final String STATE_TOOLBAR_TITLE = "toolbar_title";
    private static final String STATE_VIEWPAGER_VISIBLE = "viewpager_visible";

    private boolean needToRestoreState = false;
    private Bundle savedState = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         EdgeToEdge.enable(this);  // Not needed for DrawerLayout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.toolbar.setTitle(R.string.app_name);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        Objects.requireNonNull(binding.toolbar.getOverflowIcon()).setTint(Color.WHITE);

        setSupportActionBar(binding.toolbar);


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                binding.main,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        binding.main.addDrawerListener(mDrawerToggle);


        mDrawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);  // Make he toggle same color as other toolbar content



        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //set viewpager2
        articleAdapter = new NewsArticleAdapter(articleList);
        binding.viewPager.setAdapter(articleAdapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);


        //initialize list
        filteredList = new ArrayList<>();
        sources = new ArrayList<>();

        if (savedInstanceState != null) {
            savedState = savedInstanceState;
            needToRestoreState = true;
            // Restore non-menu related state immediately
            restoreBasicState(savedInstanceState);
        } else {
            // Initial setup only if there's no saved state
            binding.viewPager.setVisibility(View.GONE);
            NewsSourceVolley.getSourceDate(this);
        }

        //load language codes and country codes
        loadCountryCodes();
        loadLanguageCodes();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save filter states
        outState.putString(STATE_SELECTED_TOPIC, selectedTopic);
        outState.putString(STATE_SELECTED_COUNTRY, selectedCountry);
        outState.putString(STATE_SELECTED_LANGUAGE, selectedLanguage);

        // Save sources and filtered list
        outState.putParcelableArrayList(STATE_SOURCES, sources);
        outState.putParcelableArrayList(STATE_FILTERED_LIST, filteredList);

        // Save articles and current position
        outState.putParcelableArrayList(STATE_ARTICLES, articleList);
        outState.putInt(STATE_CURRENT_ARTICLE, binding.viewPager.getCurrentItem());

        // Save toolbar title and ViewPager visibility
        outState.putString(STATE_TOOLBAR_TITLE, binding.toolbar.getTitle().toString());
        outState.putBoolean(STATE_VIEWPAGER_VISIBLE, binding.viewPager.getVisibility() == View.VISIBLE);
    }

    //restore state from activity
    private void restoreBasicState(Bundle savedInstanceState) {
        // Restore filters
        selectedTopic = savedInstanceState.getString(STATE_SELECTED_TOPIC);
        selectedCountry = savedInstanceState.getString(STATE_SELECTED_COUNTRY);
        selectedLanguage = savedInstanceState.getString(STATE_SELECTED_LANGUAGE);

        // Restore sources and filtered list
        sources = savedInstanceState.getParcelableArrayList(STATE_SOURCES);
        filteredList = savedInstanceState.getParcelableArrayList(STATE_FILTERED_LIST);

        // Restore articles
        ArrayList<Article> savedArticles = savedInstanceState.getParcelableArrayList(STATE_ARTICLES);
        if (savedArticles != null && !savedArticles.isEmpty()) {
            articleList.clear();
            articleList.addAll(savedArticles);
            articleAdapter.notifyDataSetChanged();

            // Restore current article position
            int currentItem = savedInstanceState.getInt(STATE_CURRENT_ARTICLE, 0);
            binding.viewPager.setCurrentItem(currentItem, false);
        }

        // Restore toolbar title
        String toolbarTitle = savedInstanceState.getString(STATE_TOOLBAR_TITLE);
        if (toolbarTitle != null) {
            binding.toolbar.setTitle(toolbarTitle);
        }

        // Restore ViewPager visibility
        boolean viewPagerVisible = savedInstanceState.getBoolean(STATE_VIEWPAGER_VISIBLE, false);
        binding.viewPager.setVisibility(viewPagerVisible ? View.VISIBLE : View.GONE);
    }

    private void restoreCompleteState(Bundle savedInstanceState) {

        if (filteredList != null && !filteredList.isEmpty()) {
            updateDrawer(filteredList);
        }

        // Apply filters if needed
        if (selectedTopic != null || selectedCountry != null || selectedLanguage != null) {
            applyFilters();
        }
    }


    public void updateMenuWithDynamicItems(ArrayList<Source> sourceList) {

        sources = sourceList; // Store the original list
        filteredList.clear(); // Clear display list
        filteredList.addAll(sources); // Populate display list with original sources

        updateDrawer(filteredList);
    }

    private void updateDrawer(ArrayList<Source> list) {

        // crate string to update the adapter
        String[] sourceNames = new String[list.size()];

        //show error message if no sources found
        if (filteredList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("No sources found for the this filters");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        for (int i = 0; i < list.size(); i++) {
            sourceNames[i] = list.get(i).getName();
        }

        //update the toolbar title
        String title = getString(R.string.app_name) + " (" + list.size() + ")";
        binding.toolbar.setTitle(title);

        binding.leftDrawer.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, sourceNames) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                // Get the source and its category
                Source source = list.get(position);
                String category = source.getCategory();

                // Set the background color based on the category
                 textView.setTextColor(getCategoryColor(category));

                return view;
            }
        });

        binding.leftDrawer.setOnItemClickListener((parent, view, position, id) -> selectItem(position));

        if (binding.toolbar.getMenu().size() > 0) {
            updateSubMenus();
        }
    }

    private void updateSubMenus() {

        // Update topics submenu
        Menu menu = binding.toolbar.getMenu();

        // Topics submenu
        MenuItem topicsItem = menu.findItem(R.id.Topics);
        SubMenu topicsSubMenu = topicsItem.getSubMenu();
        Objects.requireNonNull(topicsSubMenu).clear(); // Clear old items if any

        // Add "All" option to the topics submenu
        topicsSubMenu.add(R.id.topics_grp, -1, Menu.NONE, "All");

        // Use a Set to eliminate duplicates
        Set<String> topicSet = new HashSet<>();
        for (Source source : sources) {
            topicSet.add(source.getCategory());
        }

        // Convert to list and sort alphabetically
        List<String> categoriesList = new ArrayList<>(topicSet);
        Collections.sort(categoriesList);

        // Add categories with colors to submenu
        for (String category : categoriesList) {
            String capitalizedCategory = capitalizeFirstLetter(category);
            MenuItem item = topicsSubMenu.add(R.id.topics_grp, category.hashCode(), Menu.NONE, capitalizedCategory);

            // Create a SpannableString to color the text To apply color to submenu
            SpannableString spannable = new SpannableString(capitalizedCategory);
            spannable.setSpan(new ForegroundColorSpan(getCategoryColor(category)),
                    0,
                    spannable.length(),
                    0);
            item.setTitle(spannable);

    }

        // Update countries submenu
        MenuItem countriesItem = menu.findItem(R.id.Countries);
        SubMenu countriesSubMenu = countriesItem.getSubMenu();
        Objects.requireNonNull(countriesSubMenu).clear(); // Clear old items if any

        // Add "All" option to the countries submenu
        countriesSubMenu.add(R.id.country_grp, -1, Menu.NONE, "All");

        // Use a Set to eliminate duplicates
        Set<String> countrySet = new HashSet<>();
        for (Source source : sources) {
            countrySet.add(source.getCountry());
        }

        //sort list
        List<String> countriesList = new ArrayList<>(countrySet);
        Collections.sort(countriesList);

        // Add countries to submenu
        for (String country : countriesList) {
            String countryCode = country.toUpperCase();

            String countryName = countryCodeToName.get(countryCode);

            if (countryName == null) {
                countryName = countryCode;  // Fallback to the country code
            }

            String capitalizedCountry = capitalizeFirstLetter(countryName);
            countriesSubMenu.add(R.id.country_grp, country.hashCode(), Menu.NONE, capitalizedCountry);
        }

        // Update languages submenu
        MenuItem languagesItem = menu.findItem(R.id.Languages);
        SubMenu languagesSubMenu = languagesItem.getSubMenu();
        Objects.requireNonNull(languagesSubMenu).clear(); // Clear old items if any

        languagesSubMenu.add(R.id.language_grp, -1, Menu.NONE, "All");

        //duplication remove
        Set<String> languageSet = new HashSet<>();
        for (Source source : sources) {
            languageSet.add(source.getLanguage());
        }

        // sort alphabetically
        List<String> languagesList = new ArrayList<>(languageSet);
        Collections.sort(languagesList);

        // Add languages to the submenu
        for (String language : languagesList) {

            String languageCode = language.toUpperCase();

            // Use the language code -> fullName
            String languageName = languageCodeToName.get(languageCode);

            if (languageName == null) {
                languageName = language;
            }

            String capitalizedLanguage = capitalizeFirstLetter(languageName);
            languagesSubMenu.add(R.id.language_grp, language.hashCode(), Menu.NONE, capitalizedLanguage);
        }
    }

    //capitalize the first letter
    private String capitalizeFirstLetter(String langName) {
        if (langName == null || langName.isEmpty()) {
            return langName;
        }
        return langName.substring(0, 1).toUpperCase() + langName.substring(1).toLowerCase();
    }


    private void selectItem(int position) {

        // Set the ViewPager2 visibility to VISIBLE
        binding.viewPager.setVisibility(View.VISIBLE);
        Source selectedSource = filteredList.get(position);
        binding.main.closeDrawer(binding.cLayout);

        // Update the Toolbar title with the selected source's name
        binding.toolbar.setTitle(selectedSource.getName());  // Set title to the selected source name

//        // Fetch articles for the selected source
        NewsArticleVolley.getArticleData(this, selectedSource.getId());

        // Scroll to the first article when articles are loaded
        binding.viewPager.setCurrentItem(0, false);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        if (needToRestoreState && savedState != null) {
            restoreCompleteState(savedState);
            needToRestoreState = false;
            savedState = null;
        }

        return true;
    }

    public static void downloadFailed() {
        Log.d(TAG, "downloadFailed: ");

    }

    private void resetDisplayList() {
        filteredList.clear();
        filteredList.addAll(sources);
        updateDrawer(filteredList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Check drawer first!
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        if (item.getItemId() == R.id.clearAll) {
            selectedTopic = null;
            selectedCountry = null;
            selectedLanguage = null;
            resetDisplayList();
        }

        // Handle Topics, Countries, and Languages submenus
        if (item.getGroupId() == R.id.topics_grp) {
            if (Objects.equals(item.getTitle(), "All")) {
                selectedTopic = null;  // Reset the topic filter
            } else {
                selectedTopic = Objects.requireNonNull(item.getTitle()).toString();
            }
        } else if (item.getGroupId() == R.id.country_grp) {
            if (Objects.equals(item.getTitle(), "All")) {
                selectedCountry = null;  // Reset the country filter
            } else {
                selectedCountry = Objects.requireNonNull(item.getTitle()).toString();
            }
        } else if (item.getGroupId() == R.id.language_grp) {
            if (Objects.equals(item.getTitle(), "All")) {
                selectedLanguage = null;  // Reset the language filter
            } else {
                selectedLanguage = Objects.requireNonNull(item.getTitle()).toString();
            }
        }

        // Apply filters
        applyFilters();

        return super.onOptionsItemSelected(item);
    }

    private void applyFilters() {
        ArrayList<Source> filteredSources = new ArrayList<>();

        if (selectedTopic == null && selectedCountry == null && selectedLanguage == null) {
            filteredSources.addAll(sources);
        } else {
            for (Source source : sources) {
                boolean matchesTopic = selectedTopic == null || source.getCategory().equalsIgnoreCase(selectedTopic);
                boolean matchesCountry = selectedCountry == null ||
                        Objects.requireNonNull(countryCodeToName.get(source.getCountry().toUpperCase())).equalsIgnoreCase(selectedCountry);
                boolean matchesLanguage = selectedLanguage == null ||
                        Objects.requireNonNull(languageCodeToName.get(source.getLanguage().toUpperCase())).equalsIgnoreCase(selectedLanguage);

                if (matchesTopic && matchesCountry && matchesLanguage) {
                    filteredSources.add(source);
                }
            }
        }

        // Update the display list with filtered sources
        filteredList.clear();
        filteredList.addAll(filteredSources);
        updateDrawer(filteredList);
    }


    //method to load the language code
    private void loadLanguageCodes() {
        try {
            String jsonContent = readRawResource(R.raw.language_codes);
            languageCodeToName = parseLanguageCodes(jsonContent);
        } catch (Exception e) {
            Log.e(TAG, "Error loading language codes: ", e);
        }
    }

    private void loadCountryCodes() {
        try {
            String jsonContent = readRawResource(R.raw.country_codes);
            countryCodeToName = parseCountryCodes(jsonContent);
        } catch (Exception e) {
            Log.d(TAG, "Error loading country codes: ", e);
        }
    }
    //method to load the countryCode
    private String readRawResource(int resourceId) throws IOException {
        try (InputStream inputStream = getResources().openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }
    }

    private Map<String, String> parseLanguageCodes(String jsonContent) throws JSONException {
        Map<String, String> codeToName = new HashMap<>();
        JSONObject jsonObject = new JSONObject(jsonContent);
        JSONArray languages = jsonObject.getJSONArray("languages");

        for (int i = 0; i < languages.length(); i++) {
            JSONObject language = languages.getJSONObject(i);
            String code = language.getString("code");
            String name = language.getString("name");
            codeToName.put(code.toUpperCase(), name);
        }
        return codeToName;
    }

    private Map<String, String> parseCountryCodes(String jsonContent) throws JSONException {
        Map<String, String> codeToName = new HashMap<>();
        JSONObject json = new JSONObject(jsonContent);
        JSONArray countries = json.getJSONArray("countries");

        for (int i = 0; i < countries.length(); i++) {
            JSONObject country = countries.getJSONObject(i);
            String code = country.getString("code");
            String name = country.getString("name");
            codeToName.put(code, name);
        }

        Log.d(TAG, "Loaded country codes: " + codeToName);
        return codeToName;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateArticleData(ArrayList<Article> articleListIn) {
        // To store fetched articles
        if (articleListIn == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Data loader failed");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        articleList.clear();
        articleList.addAll(articleListIn);
        articleAdapter.notifyDataSetChanged();


        binding.viewPager.setCurrentItem(0, false); // Scroll to the first article

    }

    //apply color in the drawer according category
    private int getCategoryColor(String category) {
        if (category == null) return Color.BLACK;

        String normalizedCategory = category.toLowerCase();

        // If we already have a color for this category, return it
        if (categoryColorMap.containsKey(normalizedCategory)) {
            return categoryColorMap.get(normalizedCategory);
        }

        // Generate a new random color
        Random random = new Random(normalizedCategory.hashCode()); // Using hashCode as seed ensures same category gets same color

        // Generate bright, distinguishable colors
        float hue = random.nextFloat() * 360; // Random hue (0-360)
        float saturation = 0.7f + random.nextFloat() * 0.3f; // High saturation (0.7-1.0)
        float brightness = 0.5f + random.nextFloat() * 0.3f; // Medium-high brightness (0.5-0.8)

        // Convert HSB to RGB color
        int color = Color.HSVToColor(new float[]{hue, saturation, brightness});

        // Store the color for future use
        categoryColorMap.put(normalizedCategory, color);

        return color;
    }

    //open article in the browser
    public void linkToBrowser(View view) {
        Article clickedArticle = (Article) view.getTag();

        if (clickedArticle != null && clickedArticle.getUrl() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickedArticle.getUrl()));
            startActivity(intent);  // Launch the URL
        }
    }

}