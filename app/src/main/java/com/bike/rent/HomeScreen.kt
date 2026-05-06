package com.bike.rent

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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userHash by sessionManager.userHash.collectAsState(initial = null)
    
    var clientData by remember { mutableStateOf<ClientResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userHash) {
        userHash?.let { hash ->
            try {
                val response = RetrofitClient.apiService.getClientInfo(hash = hash)
                if (response.isSuccessful) {
                    clientData = response.body()
                } else {
                    error = "Erro ao carregar dados: ${response.code()}"
                }
            } catch (e: Exception) {
                error = "Falha na conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error!!, color = Color.Red)
        }
    } else {
        clientData?.let { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Panel 1: Name
                        InfoRow(
                            icon = Icons.Default.Person,
                            iconContainerColor = Color(0xFFE3F2FD),
                            iconColor = Color(0xFF1976D2),
                            label = "Cliente",
                            value = data.nome
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)

                        // New Panel: Due Date
                        data.parcelas?.let { parcelas ->
                            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                            InfoRow(
                                icon = Icons.Default.Event,
                                iconContainerColor = Color(0xFFFFF3E0),
                                iconColor = Color(0xFFFF9800),
                                label = "Próximo vencimento",
                                value = dateFormatter.format(parcelas.proximaDtPrevistaPgto)
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)
                        }

                        // Panel 2: Installment
                        data.parcelas?.let { parcelas ->
                            InfoRow(
                                icon = Icons.Default.CalendarToday,
                                iconContainerColor = Color(0xFFF3E5F5),
                                iconColor = Color(0xFF7B1FA2),
                                label = "Parcela",
                                value = "${parcelas.proximaParcela} / ${parcelas.total}",
                                valueColor = Color(0xFF1976D2)
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray
                            )

                            // Panel 3: Value
                            val currencyFormatter =
                                NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                            InfoRow(
                                icon = Icons.Default.AttachMoney,
                                iconContainerColor = Color(0xFFE8F5E9),
                                iconColor = Color(0xFF388E3C),
                                label = "Valor da cobrança",
                                value = currencyFormatter.format(parcelas.proximoVlParcela),
                                valueColor = Color(0xFF388E3C),
                                valueSize = 22.sp
                            )
                        } ?: run {
                            Text(
                                text = "Sem parcelas pendentes",
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Escolha a forma de pagamento",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF212121)
                )

                // Panel 4: Buttons Pix and Card
                PaymentButton(
                    text = "Pagar com PIX",
                    subtext = "Aprovação imediata",
                    icon = Icons.Default.QrCode,
                    containerColor = Color(0xFF4285F4),
                    onClick = {}
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                PaymentButton(
                    text = "Pagar com Cartão",
                    subtext = "Crédito à vista",
                    icon = Icons.Default.CreditCard,
                    containerColor = Color(0xFF0D47A1),
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Panel 5: Informações Importantes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Informações importantes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1976D2)
                            )
                            Text(
                                text = "O pagamento desta cobrança após o vencimento pode gerar juros e encargos.",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Panel 6: History Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Ver histórico de pagamentos",
                            modifier = Modifier.weight(1.0f),
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.LightGray
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
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Seus dados estão protegidos com segurança de ponta a ponta",
                        fontSize = 11.sp,
                        color = Color.Gray,
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
    valueColor: Color = Color.Black,
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
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
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
