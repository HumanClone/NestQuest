using Microsoft.AspNetCore.Mvc;
using FireSharp.Config;
using FireSharp.Interfaces;
using FireSharp.Response;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using API.Models;

namespace TimeWise.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class BirdController : ControllerBase
    {
        static IFirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "AIzaSyCESmHZ8Sj1R6b5qlhMQRTldOd1nodJKwU",
            BasePath = "https://opsc-8b95e-default-rtdb.firebaseio.com"
        };
        IFirebaseClient client = new FireSharp.FirebaseClient(config);

        private readonly ILogger<BirdController> _logger;

        public BirdController(ILogger<BirdController> logger)
        {
            _logger = logger;
        }

        [HttpPost("AddBird")]
        public void AddBird([FromBody] Bird bird)
        {

            var data = bird;
            PushResponse response = client.Push("birds/", data);
            data.BirdId = response.Result.name;
            SetResponse setResponse = client.Set("birds/" + data.BirdId, data);
            Console.WriteLine("status Code: " + setResponse.StatusCode);
            if (setResponse.StatusCode == System.Net.HttpStatusCode.OK)
            {
                ModelState.AddModelError(string.Empty, "Added Succesfully");
            }
            else
            {
                ModelState.AddModelError(string.Empty, "Something went wrong!!");
            }
        }
        [HttpPost("EditBird")]
        public void EditBird(string? BirdId, [FromBody] Bird bird)
        {
            FirebaseResponse FireResponse = client.Get("birds/" + BirdId);
            Bird data = JsonConvert.DeserializeObject<Bird>(FireResponse.Body);
            data.BirdId = BirdId;
            if(bird.Name != null)
            {
                data.Name = bird.Name;
            }
            if(bird.ScientificName!= null)
            {
                data.ScientificName = bird.ScientificName;
            }
            SetResponse response = client.Set("birds/" + BirdId, data);

        }

        [HttpGet("GetBird")]
        public Bird GetBird(string? BirdId)
        {
            FirebaseResponse response = client.Get("birds/" + BirdId);
            Bird data = JsonConvert.DeserializeObject<Bird>(response.Body);
            return data;
        }
        [HttpGet("GetAllBirds")]
        public List<Bird> GetAllBirds()
        {
            FirebaseResponse response = client.Get("timesheets");
            dynamic data = JsonConvert.DeserializeObject<dynamic>(response.Body);
            var list = new List<Bird>();
            if (data != null)
            {
                foreach (var item in data)
                {
                    list.Add(JsonConvert.DeserializeObject<Bird>(((JProperty)item).Value.ToString()));
                }
            }
            return list;
        }
        [HttpDelete("DeleteBird")]
        public void Delete(string? BirdId)
        {
            FirebaseResponse response = client.Delete("birds/" + BirdId);
        }
    }
}