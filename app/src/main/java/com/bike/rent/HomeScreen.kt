package com.bike.rent

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*

@Composable
fun HomeScreen(onShowHistory: (List<MovimentoResponse>, String?) -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userHash by sessionManager.userHash.collectAsState(initial = null)
    
    var clientData by remember { mutableStateOf<ClientResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(userHash, refreshTrigger) {
        Log.d("HomeScreen", "LaunchedEffect triggered with userHash: $userHash, refreshTrigger: $refreshTrigger")
        if (userHash == "LOADING" || userHash.isNullOrBlank()) {
            Log.d("HomeScreen", "userHash is LOADING or null/blank, skipping fetch")
            return@LaunchedEffect
        }

        val hash = userHash!!
        try {
            isLoading = true
            Log.d("HomeScreen", "Calling getClientInfo with hash: $hash")
            val response = RetrofitClient.apiService.getClientInfo(hash = hash)
            Log.d("HomeScreen", "Response code: ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("HomeScreen", "Response body: $body")
                if (body != null) {
                    clientData = body
                } else {
                    error = "Resposta do servidor vazia"
                }
            } else {
                error = "Erro ao carregar dados: ${response.code()}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error = "Falha na conexão (${e.javaClass.simpleName}): ${e.message ?: "Erro desconhecido"}"
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error!!, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        }
    } else {
        clientData?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Panel 1: Name
                        InfoRow(
                            icon = Icons.Default.Person,
                            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            label = "Cliente",
                            value = data.nome ?: "N/A"
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // New Panel: Due Date
                        data.parcelas?.let { parcelas ->
                            Surface(
                                onClick = { refreshTrigger++ },
                                color = Color.Transparent
                            ) {
                                InfoRow(
                                    icon = Icons.Default.Event,
                                    iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    label = "Próximo vencimento",
                                    value = parcelas.proximaDtPrevistaPgto.formatAsDate()
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        }

                        // Panel 2: Installment
                        data.parcelas?.let { parcelas ->
                            InfoRow(
                                icon = Icons.Default.CalendarToday,
                                iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                iconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                label = "Parcela no mês",
                                value = "${parcelas.parcelaNoMes ?: "0"} / ${parcelas.parcelasNoMes ?: "0"}",
                                valueColor = MaterialTheme.colorScheme.primary
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Panel 3: Value
                            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                            InfoRow(
                                icon = Icons.Default.AttachMoney,
                                iconContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                label = "Valor da cobrança",
                                value = currencyFormatter.format(parcelas.proximoVlParcela.toSafeDouble()),
                                valueColor = MaterialTheme.colorScheme.primary,
                                valueSize = 22.sp
                            )
                        } ?: run {
                            Text(
                                text = "Sem parcelas pendentes",
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Escolha a forma de pagamento",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Panel 4: Buttons Pix and Card
                PaymentButton(
                    text = "pix",
                    subtext = "Aprovação imediata",
                    icon = Icons.Default.QrCode,
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {}
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                PaymentButton(
                    text = "card",
                    subtext = "Crédito à vista",
                    icon = Icons.Default.CreditCard,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Panel 5: Informações Importantes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.infoContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.info,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Informações importantes",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.info
                            )
                            Text(
                                text = "O pagamento desta cobrança após o vencimento pode gerar juros e encargos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Panel 6: History Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        data.parcelas?.let { p ->
                            onShowHistory(p.movimentos ?: emptyList(), p.proximaParcela)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Ver histórico de pagamentos",
                            modifier = Modifier.weight(1.0f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                // Footer
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Seus dados estão protegidos com segurança de ponta a ponta",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    valueSize: androidx.compose.ui.unit.TextUnit = 18.sp
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconContainerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value,
                fontSize = valueSize,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
fun PaymentButton(
    text: String,
    subtext: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(72.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (text == "pix") "Pagar com Pix" else if (text == "card") "Pagar com Cartão" else text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(text = subtext, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

val ColorScheme.infoContainer: Color
    get() = Color(0xFFD1E4FF)

val ColorScheme.info: Color
    get() = Color(0xFF005DB4)

