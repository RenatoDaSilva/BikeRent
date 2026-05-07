package com.bike.rent

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(movimentos: List<MovimentoResponse>, onBack: () -> Unit) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Pagamentos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (movimentos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum histórico encontrado", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(scrollState),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Header
                    item {
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableHeaderCell("Parc", 50.dp)
                            TableHeaderCell("Vencimento", 90.dp)
                            TableHeaderCell("Pagamento", 90.dp)
                            TableHeaderCell("Valor Parcela", 100.dp)
                            TableHeaderCell("Multa", 80.dp)
                            TableHeaderCell("Encargos", 80.dp)
                            TableHeaderCell("Valor Pago", 100.dp)
                        }
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }

                    items(movimentos) { movimento ->
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(movimento.parcela ?: "-", 50.dp)
                            TableCell(movimento.dtPrevistaPgto.formatAsDate(), 90.dp)
                            TableCell(movimento.dtPagamento.formatAsDate(), 90.dp)
                            TableCell(currencyFormatter.format(movimento.vlParcela.toSafeDouble()), 100.dp)
                            TableCell(currencyFormatter.format(movimento.multa.toSafeDouble()), 80.dp)
                            TableCell(currencyFormatter.format(movimento.encargos.toSafeDouble()), 80.dp)
                            TableCell(
                                text = currencyFormatter.format(movimento.vlPago.toSafeDouble()),
                                width = 100.dp,
                                color = if (movimento.vlPago.toSafeDouble() > 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
                                weight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    weight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontSize = 13.sp,
        color = color,
        fontWeight = weight
    )
}
