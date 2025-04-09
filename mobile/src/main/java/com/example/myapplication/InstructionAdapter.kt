package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class InstructionAdapter(private val instructions: Array<String>) :
    RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_instruction, parent, false)
        return InstructionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionViewHolder, position: Int) {
        holder.bind(instructions[position], position + 1)
    }

    override fun getItemCount(): Int = instructions.size

    class InstructionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStep: TextView = itemView.findViewById(R.id.tvStep)
        private val tvInstruction: TextView = itemView.findViewById(R.id.tvInstruction)

        fun bind(instruction: String, stepNumber: Int) {
            tvStep.text = "Step $stepNumber"
            tvInstruction.text = instruction
        }
    }
}
