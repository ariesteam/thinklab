/**
 * SimilePrologReaderTokenManager.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Gary W. Johnson, Jr.
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.dynamicmodelling.simile;

public class SimilePrologReaderTokenManager implements SimilePrologReaderConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x20000L) != 0L)
         {
            jjmatchedKind = 27;
            return 19;
         }
         if ((active0 & 0xfce000L) != 0L)
         {
            jjmatchedKind = 27;
            return 36;
         }
         if ((active0 & 0x10000L) != 0L)
         {
            jjmatchedKind = 27;
            return 24;
         }
         if ((active0 & 0x1000L) != 0L)
            return 9;
         return -1;
      case 1:
         if ((active0 & 0x100000L) != 0L)
            return 36;
         if ((active0 & 0x10000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 1;
            return 23;
         }
         if ((active0 & 0xeee000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 1;
            return 36;
         }
         return -1;
      case 2:
         if ((active0 & 0x20000L) != 0L)
            return 36;
         if ((active0 & 0xede000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 2;
            return 36;
         }
         return -1;
      case 3:
         if ((active0 & 0xce000L) != 0L)
         {
            if (jjmatchedPos != 3)
            {
               jjmatchedKind = 27;
               jjmatchedPos = 3;
            }
            return 36;
         }
         if ((active0 & 0xe10000L) != 0L)
            return 36;
         return -1;
      case 4:
         if ((active0 & 0x44000L) != 0L)
            return 36;
         if ((active0 & 0x48a000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 4;
            return 36;
         }
         return -1;
      case 5:
         if ((active0 & 0x402000L) != 0L)
            return 36;
         if ((active0 & 0x88000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 5;
            return 36;
         }
         return -1;
      case 6:
         if ((active0 & 0x88000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 6;
            return 36;
         }
         return -1;
      case 7:
         if ((active0 & 0x88000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 7;
            return 36;
         }
         return -1;
      case 8:
         if ((active0 & 0x88000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 8;
            return 36;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 3);
      case 41:
         return jjStopAtPos(0, 4);
      case 44:
         return jjStopAtPos(0, 10);
      case 45:
         return jjStopAtPos(0, 11);
      case 46:
         return jjStopAtPos(0, 9);
      case 61:
         return jjStartNfaWithStates_0(0, 12, 9);
      case 91:
         return jjStopAtPos(0, 5);
      case 93:
         return jjStopAtPos(0, 6);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0xc00000L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x100000L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x40000L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0x8000L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x84000L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x2000L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x200000L);
      case 123:
         return jjStopAtPos(0, 7);
      case 125:
         return jjStopAtPos(0, 8);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000L);
      case 102:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(1, 20, 36);
         break;
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0xc00000L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x16000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x28000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 99:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(2, 17, 36);
         break;
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x80000L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0xc000L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0xc00000L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(3, 16, 36);
         else if ((active0 & 0x800000L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 3;
         }
         return jjMoveStringLiteralDfa4_0(active0, 0x480000L);
      case 107:
         return jjMoveStringLiteralDfa4_0(active0, 0x40000L);
      case 110:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(3, 21, 36);
         break;
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x8000L);
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x80000L);
      case 115:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(4, 14, 36);
         else if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(4, 18, 36);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(5, 13, 36);
         return jjMoveStringLiteralDfa6_0(active0, 0x80000L);
      case 102:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(5, 22, 36);
         break;
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa7_0(active0, 0x80000L);
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x80000L);
      case 105:
         return jjMoveStringLiteralDfa8_0(active0, 0x8000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa9_0(active0, 0x88000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 115:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(9, 15, 36);
         else if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(9, 19, 36);
         break;
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 36;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 24:
               case 27:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 27)
                     kind = 27;
                  jjCheckNAddTwoStates(27, 28);
                  break;
               case 36:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 27)
                     kind = 27;
                  jjCheckNAddTwoStates(27, 28);
                  break;
               case 19:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 27)
                     kind = 27;
                  jjCheckNAddTwoStates(27, 28);
                  break;
               case 23:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 27)
                     kind = 27;
                  jjCheckNAddTwoStates(27, 28);
                  break;
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 29)
                        kind = 29;
                     jjCheckNAddStates(0, 2);
                  }
                  else if ((0x5000000200000000L & l) != 0L)
                  {
                     if (kind > 26)
                        kind = 26;
                  }
                  else if ((0x8c0000000000L & l) != 0L)
                  {
                     if (kind > 25)
                        kind = 25;
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 2)
                        kind = 2;
                  }
                  else if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 14;
                  else if (curChar == 61)
                     jjCheckNAdd(9);
                  else if (curChar == 39)
                     jjCheckNAddStates(3, 5);
                  if (curChar == 33)
                     jjCheckNAdd(9);
                  else if (curChar == 62)
                     jjCheckNAdd(9);
                  else if (curChar == 60)
                     jjCheckNAdd(9);
                  else if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 1:
                  if (curChar == 10 && kind > 2)
                     kind = 2;
                  break;
               case 2:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 3:
                  if (curChar == 39)
                     jjCheckNAddStates(3, 5);
                  break;
               case 4:
                  if ((0xffffff7fffffffffL & l) != 0L)
                     jjCheckNAddStates(3, 5);
                  break;
               case 6:
                  if (curChar == 39 && kind > 24)
                     kind = 24;
                  break;
               case 7:
                  if ((0x8c0000000000L & l) != 0L && kind > 25)
                     kind = 25;
                  break;
               case 8:
                  if ((0x5000000200000000L & l) != 0L && kind > 26)
                     kind = 26;
                  break;
               case 9:
                  if (curChar == 61 && kind > 26)
                     kind = 26;
                  break;
               case 10:
                  if (curChar == 60)
                     jjCheckNAdd(9);
                  break;
               case 11:
                  if (curChar == 62)
                     jjCheckNAdd(9);
                  break;
               case 12:
                  if (curChar == 61)
                     jjCheckNAdd(9);
                  break;
               case 13:
                  if (curChar == 33)
                     jjCheckNAdd(9);
                  break;
               case 14:
                  if (curChar == 38 && kind > 26)
                     kind = 26;
                  break;
               case 15:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 27)
                     kind = 27;
                  jjCheckNAddTwoStates(28, 29);
                  break;
               case 30:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 29)
                     kind = 29;
                  jjCheckNAddStates(0, 2);
                  break;
               case 31:
                  if (curChar == 46)
                     jjCheckNAdd(32);
                  break;
               case 32:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 29)
                     kind = 29;
                  jjCheckNAddTwoStates(32, 33);
                  break;
               case 34:
                  if (curChar == 45)
                     jjCheckNAdd(35);
                  break;
               case 35:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 29)
                     kind = 29;
                  jjCheckNAdd(35);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 24:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 27)
                        kind = 27;
                     jjCheckNAddTwoStates(26, 27);
                  }
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 36:
               case 26:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 27)
                     kind = 27;
                  jjCheckNAddTwoStates(26, 27);
                  break;
               case 19:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 27)
                        kind = 27;
                     jjCheckNAddTwoStates(26, 27);
                  }
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 23:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 27)
                        kind = 27;
                     jjCheckNAddTwoStates(26, 27);
                  }
                  if (curChar == 116)
                  {
                     if (kind > 26)
                        kind = 26;
                  }
                  break;
               case 0:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 27)
                        kind = 27;
                     jjCheckNAddTwoStates(26, 27);
                  }
                  else if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 16;
                  else if (curChar == 94)
                  {
                     if (kind > 25)
                        kind = 25;
                  }
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 24;
                  else if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 21;
                  else if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 4:
                  jjAddStates(3, 5);
                  break;
               case 5:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 7:
                  if (curChar == 94 && kind > 25)
                     kind = 25;
                  break;
               case 16:
                  if (curChar == 124 && kind > 26)
                     kind = 26;
                  break;
               case 17:
                  if (curChar == 124)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if (curChar == 100 && kind > 26)
                     kind = 26;
                  break;
               case 20:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if (curChar == 114 && kind > 26)
                     kind = 26;
                  break;
               case 22:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 25:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 28:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 33:
                  if (curChar == 101)
                     jjAddStates(6, 7);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(3, 5);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 36 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   30, 31, 33, 4, 5, 6, 34, 35, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, "\50", "\51", "\133", "\135", "\173", "\175", "\56", "\54", 
"\55", "\75", "\163\157\165\162\143\145", "\162\157\157\164\163", 
"\160\162\157\160\145\162\164\151\145\163", "\156\157\144\145", "\141\162\143", "\154\151\156\153\163", 
"\162\145\146\145\162\145\156\143\145\163", "\151\146", "\164\150\145\156", "\145\154\163\145\151\146", 
"\145\154\163\145", null, null, null, null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x2ffffffdL, 
};
static final long[] jjtoSkip = {
   0x2L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[36];
private final int[] jjstateSet = new int[72];
protected char curChar;
public SimilePrologReaderTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public SimilePrologReaderTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 36; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100000000L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
