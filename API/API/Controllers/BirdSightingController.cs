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
    public class BirdSightingSightingController : ControllerBase
    {
        static IFirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "AIzaSyCESmHZ8Sj1R6b5qlhMQRTldOd1nodJKwU",
            BasePath = "https://opsc-8b95e-default-rtdb.firebaseio.com"
        };
        IFirebaseClient client = new FireSharp.FirebaseClient(config);

        private readonly ILogger<BirdSightingSightingController> _logger;

        public BirdSightingSightingController(ILogger<BirdSightingSightingController> logger)
        {
            _logger = logger;
        }

        [HttpPost("AddBirdSighting")]
        public void AddBirdSighting([FromBody] BirdSighting birdSighting)
        {

            var data = birdSighting;
            PushResponse response = client.Push("birdSightings/", data);
            data.BirdSightingId = response.Result.name;
            SetResponse setResponse = client.Set("birdSightings/" + data.BirdSightingId, data);
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
        [HttpPost("EditBirdSighting")]
        public void EditBirdSighting(string? BirdSightingId, [FromBody] BirdSighting birdSighting)
        {
            FirebaseResponse FireResponse = client.Get("birdSightings/" + BirdSightingId);
            BirdSighting data = JsonConvert.DeserializeObject<BirdSighting>(FireResponse.Body);
            data.BirdSightingId = BirdSightingId;
            if(birdSighting.UserId != null)
            {
                data.UserId = birdSighting.UserId;
            }
            if (birdSighting.BirdId != null)
            {
                data.BirdId = birdSighting.BirdId;
            }
            if (birdSighting.DateSeen != null)
            {
                data.DateSeen = birdSighting.DateSeen;
            }
            if (birdSighting.Coordinates != null)
            {
                data.Coordinates = birdSighting.Coordinates;
            }
            SetResponse response = client.Set("birdSightings/" + BirdSightingId, data);

        }

        [HttpGet("GetBirdSighting")]
        public BirdSighting GetBirdSighting(string? BirdSightingId)
        {
            FirebaseResponse response = client.Get("birdSightings/" + BirdSightingId);
            BirdSighting data = JsonConvert.DeserializeObject<BirdSighting>(response.Body);
            return data;
        }
        [HttpGet("GetAllBirdSightings")]
        public List<BirdSighting> GetAllBirdSightings()
        {
            FirebaseResponse response = client.Get("birdSightings");
            dynamic data = JsonConvert.DeserializeObject<dynamic>(response.Body);
            var list = new List<BirdSighting>();
            if (data != null)
            {
                foreach (var item in data)
                {
                    list.Add(JsonConvert.DeserializeObject<BirdSighting>(((JProperty)item).Value.ToString()));
                }
            }
            return list;
        }
        [HttpDelete("DeleteBirdSighting")]
        public void Delete(string? BirdSightingId)
        {
            FirebaseResponse response = client.Delete("birdSightings/" + BirdSightingId);
        }
    }
}