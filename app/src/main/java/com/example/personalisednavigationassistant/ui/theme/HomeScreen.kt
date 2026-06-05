package com.example.personalisednavigationassistant.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.ArrowBack
import com.example.personalisednavigationassistant.R
import android.Manifest
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint



data class Monument(val id: String, val name: String, val imageRes: Int, val shortDescription: String, val nextPoi: String, val latitude: Double, val longitude: Double)


val monumentsList = listOf(
    Monument("parthenon_01", "Parthenon", R.drawable.parthenon, "Ο Παρθενώνας είναι ναός, χτισμένος προς τιμήν της θεάς Αθηνάς, προστάτιδας της πόλης της Αθήνας. Αποτελεί το λαμπρότερο μνημείο της Αθηναϊκής πολιτείας.",
        "Επόμενη στάση: Ερέχθειο",37.9715, 23.7267),
    Monument("athens_nike", "Temple of Athens Victory", R.drawable.nike, "Ο Ναός της Αθηνάς Νίκης είναι ένας κομψός ιωνικός ναός χτισμένος στην άκρη του βράχου της Ακρόπολης, αφιερωμένος στη νίκη των Αθηναίων κατά των Περσών.",
        "Επόμενη στάση: Προπύλαια", 37.9715, 23.7249),
    Monument("propylaia", "Propylaia", R.drawable.propylaia, "Τα Προπύλαια αποτελούν τη μνημειώδη και εντυπωσιακή είσοδο της Ακρόπολης. Είναι ένα αρχιτεκτονικό αριστούργημα που προετοίμαζε τους επισκέπτες για το μεγαλείο του ιερού βράχου.",
        "Επόμενη στάση: Παρθενώνας", 37.9716, 23.7251),
    Monument("erechtheion", "Erechtheion", R.drawable.erechtheion, "Το Ερέχθειο είναι ένας ιερός ναός, διάσημος για τις Καρυάτιδες, τα γλυπτά σε σχήμα γυναικών που στηρίζουν την οροφή του. Ήταν το πιο ιερό και μυστηριακό σημείο όλης της Ακρόπολης.",
        "Επόμενη στάση: Θέατρο Διονύσου", 37.9721, 23.7266),
    Monument("dionysius", "Dionysius Theatre", R.drawable.dionysius, "Θεωρείται το αρχαιότερο θέατρο του κόσμου και η γενέτειρα του δράματος. Εδώ παρουσιάστηκαν για πρώτη φορά τα σπουδαία έργα του Αισχύλου, του Σοφοκλή και του Ευριπίδη.",
        "Επόμενη στάση: Ωδείο Ηρώδου Αττικού", 37.9703, 23.7277),
    Monument("odeon", "Herodotus Odeon", R.drawable.odeon, "Το Ηρώδειο είναι ένα εντυπωσιακό ρωμαϊκό ωδείο στη νοτιοδυτική πλαγιά της Ακρόπολης. Μέχρι και σήμερα, φιλοξενεί παραστάσεις και συναυλίες κάτω από τον αττικό ουρανό.",
        "Επόμενη στάση: Μουσείο Ακρόπολης", 37.9708, 23.7246)
)
@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onMonumentClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit, // <--- ΝΕΟ
    onNavigateToHelp: () -> Unit,     // <--- ΝΕΟ
    onLogout: () -> Unit              // <--- ΝΕΟ
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<Location?>(null) }

    //Launcher για να ζητήσει τις άδειες τοποθεσίας από τον χρήστη
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    userLocation = location
                }
            }
        }
    )


    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    val processedMonuments = remember(searchQuery, userLocation) {
        val filtered = if (searchQuery.isEmpty()) {
            monumentsList
        } else {
            monumentsList.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        if (userLocation != null) {
            filtered.sortedBy { monument ->
                val results = FloatArray(1)
                Location.distanceBetween(
                    userLocation!!.latitude, userLocation!!.longitude,
                    monument.latitude, monument.longitude,
                    results
                )
                results[0]
            }
        } else {
            filtered
        }
    }


    // Το πλαϊνό μενού (Drawer)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFFEA4335)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Αρχαίος Ξεναγός", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Help Center") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToHelp()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Welcome", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onScanClick,
                    containerColor = Color(0xFF0038FD), // Το μπλε του Figma σου
                    contentColor = Color.White
                ) {
                    Text("Scan", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Please Enter The City") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(processedMonuments) { monument ->
                        MonumentCard(
                            monument = monument,
                            userLocation = userLocation,
                            onClick = { onMonumentClick(monument.id) }
                        )
                    }
                }
            }
        }
    }
}


//Η μεμονωμένη Κάρτα του Μνημείου
@Composable
fun MonumentCard(monument: Monument, userLocation: Location?, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = monument.imageRes,
            contentDescription = monument.name,
            contentScale = ContentScale.Crop, // Κόβει την εικόνα για να γεμίσει σωστά το τετράγωνο
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = monument.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (userLocation != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                userLocation!!.latitude, userLocation!!.longitude,
                monument.latitude, monument.longitude,
                results
            )
            Text(
                text = "📍 ${results[0].toInt()} μέτρα μακριά",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Λογαριασμός", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Αλλαγή Γλώσσας\n• Ειδοποιήσεις\n• Απόρρητο & Ασφάλεια", color = Color.DarkGray, lineHeight = 24.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Σχετικά", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Έκδοση Εφαρμογής: 1.0.0\n• Όροι Χρήσης", color = Color.DarkGray, lineHeight = 24.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Συχνές Ερωτήσεις (FAQ)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Πώς λειτουργεί ο AI Ξεναγός;\n• Πώς σκανάρω ένα QR Code;\n• Η εφαρμογή χρειάζεται ίντερνετ;", color = Color.DarkGray, lineHeight = 28.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { /* Εικονικό κουμπί */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Επικοινωνία με Υποστήριξη")
            }
        }
    }
}