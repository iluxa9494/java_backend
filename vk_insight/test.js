const { VK } = require('vk-io');

const vk = new VK({
    token: "vk1.a.p8YrfHzunxhOvDk356c5XvP_3hDujEZV2DLnB6140IbSxxDF_oQxRm39a3UsCGV3CI-k2ZyxXpbFeJUJ975nsSlm_QK-C5cqZpqYe1ME_ZKWAKmeTXnDfTFd7uI-lMMzMHPcqWT9yAQXCmsblSfmnGod5Alj3hHeXxLXwDtJM-ePuCeibDzi60TgD0eSKbdRyz6c_j39realaeVPQGIjUA"
});
async function getUserEducationInfo(userId) {
    try {
        const response = await vk.api.users.get({
            user_ids: userId,
            fields: "schools,universities,education,activities"
        });

        console.log(response);

    } catch (error) {
        console.error(error);
    }
}

getUserEducationInfo(344396553);
