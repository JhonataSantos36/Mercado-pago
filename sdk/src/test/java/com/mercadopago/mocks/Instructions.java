package com.mercadopago.mocks;

import com.mercadopago.lite.model.Instruction;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

/**
 * Created by vaserber on 11/2/17.
 */

public class Instructions {

    private Instructions() {

    }

    public static Instruction getRapipagoInstruction() {
        String json = ResourcesUtil.getStringResource("instructions_rapipago.json");
        return JsonUtil.getInstance().fromJson(json, Instruction.class);
    }

    public static Instruction getBoletoInstructionBankTransfer() {
        String json = ResourcesUtil.getStringResource("instructions_boleto_bank_transfer.json");
        return JsonUtil.getInstance().fromJson(json, Instruction.class);
    }

    public static Instruction getBoletoInstructionTicket() {
        String json = ResourcesUtil.getStringResource("instructions_boleto_ticket.json");
        return JsonUtil.getInstance().fromJson(json, Instruction.class);
    }

    public static Instruction getRedLinkBankTransferInstruction() {
        String json = ResourcesUtil.getStringResource("instructions_redlink_bank_transfer.json");
        return JsonUtil.getInstance().fromJson(json, Instruction.class);
    }

    public static Instruction getRedLinkAtmInstruction() {
        String json = ResourcesUtil.getStringResource("instructions_redlink_atm.json");
        return JsonUtil.getInstance().fromJson(json, Instruction.class);
    }

    public static Instruction getBancomerAtmInstruction() {
        String json = ResourcesUtil.getStringResource("instructions_bancomer_atm.json");
        return JsonUtil.getInstance().fromJson(json, Instruction.class);
    }

    public static Instruction getBanamexBankTransferInstruction() {
        String json = ResourcesUtil.getStringResource("instructions_banamex_bank_transfer.json");
        return JsonUtil.getInstance().fromJson(json, Instruction.class);
    }
}
