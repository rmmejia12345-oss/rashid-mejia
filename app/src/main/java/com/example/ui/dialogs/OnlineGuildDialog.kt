package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DefaultGuildData
import com.example.model.GuildBounty
import com.example.model.Hero
import com.example.model.LeaderboardEntry
import com.example.model.OnlineAdventurer

@Composable
fun OnlineGuildDialog(
    hero: Hero,
    partyMembers: List<OnlineAdventurer>,
    allAdventurers: List<OnlineAdventurer>,
    bounties: List<GuildBounty>,
    leaderboard: List<LeaderboardEntry>,
    onDismiss: () -> Unit,
    onTogglePartyMember: (OnlineAdventurer) -> Unit,
    onClaimBounty: (GuildBounty) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Guild Lobby", "Co-op Party (${partyMembers.size}/3)", "Bounties", "Rankings")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141E30),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .border(2.dp, Color(0xFF9B59B6), RoundedCornerShape(20.dp))
                .testTag("online_guild_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = "Online", tint = Color(0xFF2ECC71), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Online Adventurers Guild", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("Aldoria Co-op Realm • Live Hub", color = Color(0xFF2ECC71), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_guild")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0D1B2A),
                    contentColor = Color(0xFFFFD700),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF9B59B6)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedTab) {
                    0 -> AdventurersLobbyTab(
                        allAdventurers = allAdventurers,
                        partyMembers = partyMembers,
                        onToggle = onTogglePartyMember
                    )
                    1 -> PartyTab(
                        hero = hero,
                        partyMembers = partyMembers,
                        onDismissMember = onTogglePartyMember
                    )
                    2 -> BountiesTab(
                        bounties = bounties,
                        onClaim = onClaimBounty
                    )
                    3 -> LeaderboardTab(leaderboard = leaderboard)
                }
            }
        }
    }
}

@Composable
private fun AdventurersLobbyTab(
    allAdventurers: List<OnlineAdventurer>,
    partyMembers: List<OnlineAdventurer>,
    onToggle: (OnlineAdventurer) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "CHAMPIONS IN ALDORIA",
            color = Color(0xFFFFD700),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Recruit adventurers into your party to receive shared combat stat auras!",
            color = Color.LightGray,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allAdventurers) { adv ->
                val isInParty = partyMembers.any { it.id == adv.id }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E2D42),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF2E3D52), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(adv.avatarColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(adv.heroClass.title.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(adv.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${adv.guildTitle} • Lv.${adv.level} ${adv.heroClass.title}", color = Color(0xFFFFD54F), fontSize = 10.sp)
                                Text(adv.status, color = Color(0xFF81C784), fontSize = 9.sp)
                            }
                        }

                        Button(
                            onClick = { onToggle(adv) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInParty) Color(0xFFE74C3C) else Color(0xFF2ECC71)
                            ),
                            modifier = Modifier.height(32.dp).testTag("btn_recruit_${adv.id}")
                        ) {
                            Text(
                                text = if (isInParty) "Leave Party" else "Invite",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PartyTab(
    hero: Hero,
    partyMembers: List<OnlineAdventurer>,
    onDismissMember: (OnlineAdventurer) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("CURRENT CO-OP PARTY", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        // Party Aura Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C3E50)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF9B59B6), RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("🌟 Active Party Synergy Buffs:", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                val atkBonus = partyMembers.sumOf { it.attackBonus }
                val defBonus = partyMembers.sumOf { it.defenseBonus }
                Text("• United Valor: +$atkBonus Bonus Attack Power to all party members", color = Color(0xFFFF7675), fontSize = 10.sp)
                Text("• Shield Formation: +$defBonus Physical Defense to all party members", color = Color(0xFF74B9FF), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Leader (You)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1B263B),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFFD700), RoundedCornerShape(10.dp))
        ) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("👑", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("${hero.name} (Party Leader)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Level ${hero.level} ${hero.heroClass.title}", color = Color(0xFFFFD54F), fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (partyMembers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No party members recruited yet. Check the Guild Lobby tab to invite allies!", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(partyMembers) { member ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E2D42),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(member.avatarColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(member.heroClass.title.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(member.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Lv.${member.level} ${member.heroClass.title} • Buffs Active", color = Color(0xFF81C784), fontSize = 9.sp)
                                }
                            }
                            IconButton(onClick = { onDismissMember(member) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.PersonRemove, contentDescription = "Dismiss", tint = Color(0xFFE74C3C), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BountiesTab(
    bounties: List<GuildBounty>,
    onClaim: (GuildBounty) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("DAILY GUILD BOUNTIES", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bounties) { bounty ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E2D42),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF2E3D52), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(bounty.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                Text("${bounty.goldReward}g ", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.Diamond, contentDescription = "Gems", tint = Color(0xFF00CED1), modifier = Modifier.size(14.dp))
                                Text("${bounty.gemReward}", color = Color(0xFF00CED1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(bounty.targetDescription, color = Color.LightGray, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { onClaim(bounty) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF39C12)),
                            modifier = Modifier.height(30.dp).align(Alignment.End).testTag("btn_claim_bounty_${bounty.id}")
                        ) {
                            Text("Claim Reward", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTab(leaderboard: List<LeaderboardEntry>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("GLOBAL HALL OF FAME", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("Ranked by Dungeon Clears and Legendary Trophies", color = Color.Gray, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(leaderboard) { entry ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E2D42),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (entry.rank) {
                                    1 -> "🥇"
                                    2 -> "🥈"
                                    3 -> "🥉"
                                    else -> "#${entry.rank}"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )
                            Column {
                                Text(entry.heroName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Lv.${entry.level} ${entry.heroClass.title}", color = Color(0xFFFFD54F), fontSize = 10.sp)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = "Trophies", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${entry.bossTrophies} Trophies (${entry.dungeonClears} Clears)", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
